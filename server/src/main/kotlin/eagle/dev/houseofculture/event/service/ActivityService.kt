package eagle.dev.houseofculture.event.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.category.service.CategoryService
import eagle.dev.houseofculture.category.util.CategoryMapper
import eagle.dev.houseofculture.enrollment.model.Enrollment
import eagle.dev.houseofculture.enrollment.model.PaymentStatus
import eagle.dev.houseofculture.enrollment.repository.EnrollmentRepository
import eagle.dev.houseofculture.enrollment.service.EnrollmentService
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.EventStatus
import eagle.dev.houseofculture.event.model.EventType
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.event.repository.SingleEventRepository
import eagle.dev.houseofculture.event.util.EventMapper
import eagle.dev.houseofculture.event.util.validator.ActivityValidator
import eagle.dev.houseofculture.event.util.validator.InstructorValidator
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.image.service.ImageService
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.openapi.model.CreateSingleEventOccurenceTs.Frequency
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.jvm.optionals.getOrElse

@Service
class ActivityService(
    private val eventRepository: EventRepository,
    private val singleEventRepository: SingleEventRepository,
    private val userRepository: UserRepository,
    private val categoryService: CategoryService,
    private val eventMapper: EventMapper,
    private val categoryMapper: CategoryMapper,
    private val imageService: ImageService,
    private val commonEventService: CommonEventService,
    private val authService: AuthService,
    private val enrollmentService: EnrollmentService,
    private val enrollmentRepository: EnrollmentRepository
) {
    val imagesPath = "events"

    fun createActivity(request: CreateActivityRequestTs): Long {
        ActivityValidator.validate(request)

        val instructor = getInstructor(request.instructorId)
        val categories = getCategories(request.categories)

        val savedActivity = with(request) {
            eventRepository.save(
                Event(
                    title,
                    EventType.MULTIPLE,
                    description,
                    categories,
                    instructor,
                    minimumAge,
                    maximumAge,
                    maxPlaces,
                    cost
                )
            )
        }

        // generate and save single events
        savedActivity.singleEvents = buildSingleEvents(savedActivity, request.occurences)

        return saveActivityWithImages(savedActivity, request.images)
    }

    fun getActivity(id: Long): ActivityResponseTs =
        commonEventService
            .getEvent(id)
            .let { eventMapper.activityToTs(it, imageService.getAllImages(it.getImagesDirectory())) }

    fun getActivitiesCategories(number: Int?): List<CategoryResponseTs> =
        eventRepository
            .findMostPopularAssociatedCategories(
                EventType.MULTIPLE,
                number?.let { PageRequest.of(0, it) }
            )
            .map(categoryMapper::categoryToTs)

    fun getPopularActivities(): PopularActivitiesResponseTs {
        val categories = eventRepository.findMostPopularAssociatedCategories(EventType.MULTIPLE, null).take(4)

        val uncategorized = eventRepository.findTop4By().map {
            eventMapper.activityToSummaryTs(it, imageService.getFirstImage(it.getImagesDirectory()))
        }
        val categorized = categories.map { category ->
            PopularActivitiesCategorizedResponseTs(
                category.id,
                category.name,
                eventRepository
                    .findTop4ByCategoriesContaining(category)
                    .map {
                        eventMapper.activityToSummaryTs(it, imageService.getFirstImage(it.getImagesDirectory()))
                    }
            )
        }

        return PopularActivitiesResponseTs(uncategorized, categorized)
    }

    fun getActivities(page: Int, pageSize: Int, text: String, categoryId: Long?): FilteredActivitiesResponseTs {
        val pageRequest = PageRequest.of(page, pageSize)
        val category = categoryId?.let(categoryService::findByIdOrNull)

        val activities = if (category != null) {
            eventRepository.findAllByTitleContainingAndCategoriesContainingAndStatus(
                text,
                category,
                EventStatus.ACTIVE,
                pageRequest
            )
        } else {
            eventRepository.findAllByTitleContainingAndStatus(text, EventStatus.ACTIVE, pageRequest)
        }

        // don't use MapStruct because it cannot map Iterable object to Non-Iterable
        return activities.let { activity ->
            FilteredActivitiesResponseTs(
                activity.content.map {
                    eventMapper.activityToSummaryTs(
                        it,
                        imageService.getFirstImage(it.getImagesDirectory())
                    )
                },
                activity.totalPages
            )
        }
    }

    fun editActivity(id: Long, request: EditActivityRequestTs): Long {
        ActivityValidator.validate(request)

        val activity = eventRepository.findById(id).orElseThrow {
            throw ObjectNotFoundException("Activity with id $id doesn't exist.")
        }

        val instructor = getInstructor(request.instructorId)
        val categories = getCategories(request.categories)

        activity.apply { updateSingleEvents(this, request.occurences) }

        with(request) {
            activity.title = title
            activity.description = description
            activity.categories = categories
            activity.instructor = instructor
            activity.minimumAge = minimumAge
            activity.maximumAge = maximumAge
            activity.maxPlaces = maxPlaces
            activity.cost = cost
        }

        return saveActivityWithImages(activity, request.images)
    }

    fun deleteActivity(id: Long) {
        val activity = eventRepository.findById(id).orElseThrow {
            throw ObjectNotFoundException("Activity with id $id doesn't exist.")
        }

        val enrollmentsForEvent = eventRepository.getEnrollmentsForEvent(activity)
        if (enrollmentsForEvent.isNotEmpty()) {
            this.sendMailsToUsers(enrollmentsForEvent, activity)

            val enrollmentsToDelete = enrollmentsForEvent
                .filter { it.event.starts > Instant.now() }

            enrollmentRepository.deleteAll(enrollmentsToDelete)
            eventRepository.delete(activity)
        }

        deleteActivityImages(activity.getImagesDirectory())
        eventRepository.delete(activity)
    }

    fun getInstructorActivity(): List<ActivitySummaryResponseTs> {
        val loggedOnUser = authService.loggedInUserOrException()
        InstructorValidator.validate(loggedOnUser)

        val currentDate = Instant.now()

        val activities = eventRepository.getAllCurrentlyOngoingActivitiesForInstructor(loggedOnUser, currentDate)

        return activities.map { eventMapper.activityToSummaryTs(it, "") }
    }

    private fun sendMailsToUsers(enrollments: List<Enrollment>, event: Event) {
        val currentDate = Instant.now()
        val userToEnrollmentsMap = enrollments.groupBy { it.client }

        userToEnrollmentsMap.forEach { (user, userEnrollments) ->
            val moneyToReturn = userEnrollments
                .filter { it.event.ends > currentDate && it.payment?.status == PaymentStatus.COMPLETED }
                .sumOf { it.event.event.cost ?: 0.0 }

            enrollmentService.sendEmailsAfterDisenrollment(event, user, moneyToReturn)
        }
    }

    // save images after saving activity in DB because file system is not transactional
    private fun saveActivityWithImages(activity: Event, images: List<String>): Long {
        return eventRepository.save(activity)
            .also { saveImages(images, activity.getImagesDirectory()) }
            .id!!
    }

    private fun saveImages(images: List<String>, path: String) {
        imageService.clearDirectory(path) // delete old images
        images.mapIndexed { index, it ->
            imageService.saveImage(it, path, "image_$index")
        }
    }

    private fun deleteActivityImages(path: String) {
        imageService.clearDirectory(path) // delete images and directory
    }

    private fun buildSingleEvents(savedActivity: Event, occurrences: List<CreateSingleEventOccurenceTs>): List<SingleEvent> {
        val events = occurrences.flatMap {
            val repeats = if (it.frequency == Frequency.SINGLE) 1 else it.repeat
            val singleEvents = mutableListOf<SingleEvent>()
            val interval = it.frequency.interval()

            for (i in 1..repeats) {
                val secondsToAdd = interval.multipliedBy(i.toLong())?.toSeconds() ?: 0

                singleEvents.add(
                    SingleEvent(
                        savedActivity,
                        it.startTime.toInstant().plusSeconds(secondsToAdd),
                        it.endTime.toInstant().plusSeconds(secondsToAdd),
                    )
                )
            }

            singleEvents
        }
        val instructorActivities = savedActivity.instructor?.let { eventRepository.findAllNoCanceledSingleEventsByInstructor(it) } ?: emptyList()
        ActivityValidator.validateCollisions(instructorActivities, events)

        return events
    }

    private fun Frequency.interval(): Duration =
        when (this) {
            Frequency.SINGLE -> Duration.ZERO
            Frequency.EVERY_WEEK -> ChronoUnit.WEEKS.duration
            Frequency.EVERY_TWO_WEEKS -> ChronoUnit.WEEKS.duration.multipliedBy(2)
            Frequency.EVERY_THREE_WEEKS -> ChronoUnit.WEEKS.duration.multipliedBy(3)
            Frequency.EVERY_MONTH -> ChronoUnit.MONTHS.duration
        }

    private fun updateSingleEvents(activity: Event, occurrences: List<SingleEventRequestTs>) {
        val idsToKeep = occurrences.map { it.id }
        val singleEventsToDelete = activity.singleEvents.filter { it.id !in idsToKeep }.map { it.id!! }

        activity.singleEvents = activity.singleEvents
            .filter { it.id !in singleEventsToDelete } // filter old single events from activity to keep in DB
            .toMutableList()
            .apply {
                addAll(
                    // add new single events from request to save in database
                    occurrences.filter { it.id == null }.map {
                        SingleEvent(activity, it.startTime.toInstant(), it.endTime.toInstant())
                    }
                )
            }

        val instructorActivities =
            activity.instructor?.let { eventRepository.findAllNoCanceledSingleEventsByInstructorNotForEvent(it, activity) }
                ?: emptyList()
        ActivityValidator.validateCollisions(instructorActivities, activity.singleEvents)

        singleEventsToDelete.forEach(singleEventRepository::deleteById)
    }

    private fun getInstructor(instructorId: Long): Client = userRepository
        .findById(instructorId)
        .getOrElse { throw ObjectNotFoundException("Instructor with id $instructorId doesn't exist.") }
        .let(ActivityValidator::validateInstructor)

    private fun getCategories(ids: List<Long>): List<Category> = try {
        categoryService.findByIds(ids)
    } catch (e: ObjectNotFoundException) {
        throw ObjectNotFoundException(e.message)
    }

    private fun Event.getImagesDirectory() =
        "$imagesPath/$id/"
}
package eagle.dev.houseofculture.enrollment.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.contact.service.ContactInfoService
import eagle.dev.houseofculture.enrollment.model.Enrollment
import eagle.dev.houseofculture.enrollment.model.PaymentStatus
import eagle.dev.houseofculture.enrollment.repository.EnrollmentRepository
import eagle.dev.houseofculture.enrollment.util.EnrollmentMapper
import eagle.dev.houseofculture.enrollment.validator.EnrollmentValidator
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.event.repository.SingleEventRepository
import eagle.dev.houseofculture.event.service.CommonEventService
import eagle.dev.houseofculture.event.util.EventMapper
import eagle.dev.houseofculture.event.util.validator.InstructorValidator
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.mail.MailSender
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.service.UserService
import eagle.dev.houseofculture.user.util.UserMapper
import eagle.dev.houseofculture.util.toInstant
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate

@Service
class EnrollmentService(
    private val eventRepository: EventRepository,
    private val singleEventRepository: SingleEventRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val authService: AuthService,
    private val userService: UserService,
    private val commonEventService: CommonEventService,
    private val eventMapper: EventMapper,
    private val userMapper: UserMapper,
    private val enrollmentMapper: EnrollmentMapper,
    private val mailSender: MailSender,
    private val contactInfoService: ContactInfoService
) {
    fun enrollToEvent(
        eventId: Long,
        request: CreateEnrollmentRequestTs
    ): List<SingleEventOccurenceTs> {
        val event = commonEventService.getEvent(eventId)

        val userToEnroll = getUserWithPermissionValidation(request.userId)

        EnrollmentValidator.validateUserAge(event, userToEnroll)

        // take selected number of single events to enroll
        // if numberOfSingleEvents is null or more than list size then return all available single events
        val singleEventsToEnroll = event.singleEvents
            .filter { !it.isFromPast && !it.isCancelled }
            .filter { singleEvent -> singleEvent.enrollments.none { it.client.id == userToEnroll.id } }
            .sortedBy { it.starts }
            .take(request.numberOfSingleEvents ?: Int.MAX_VALUE)

        EnrollmentValidator.canEnrollToSelectedSingleEvents(singleEventsToEnroll, request.numberOfSingleEvents)

        // save enrollments to database
        return singleEventsToEnroll
            .map { Enrollment(userToEnroll, it) }
            .let(enrollmentRepository::saveAll)
            .map { eventMapper.singleEventToOccurrenceResponse(it.event) }
    }

    fun getEnrollmentAvailability(eventId: Long): EnrollmentAvailabilityResponseTs {
        val event = commonEventService.getEvent(eventId)
        val loggedInUser = authService.loggedInUserOrException()

        val allowedUsersToEnroll = listOf(
            loggedInUser,
            *(loggedInUser as Client).children.toTypedArray()
        )

        return enrollmentMapper.enrollmentAvailabilityToResponse(
            event,
            allowedUsersToEnroll.map { getSingleUserEnrollmentAvailability(it, event) }
        )
    }

    private fun getSingleUserEnrollmentAvailability(
        user: User,
        event: Event
    ): UserEnrollmentAvailabilityResponseTs {
        var status = EnrollmentValidator.validateUserAgeWithStatus(event, user)
        // query is not required if user cannot enroll to event
        val availableSingleEventsToEnroll =
            if(status == EnrollmentAvailabilityStatusTs.AVAILABLE)
                singleEventRepository.getAvailableSingleEventsToEnroll(user, Instant.now(), event)
            else
                emptyList()

        // if there is no available single events then user already enrolled to all (or all are from the past)
        if(status == EnrollmentAvailabilityStatusTs.AVAILABLE && availableSingleEventsToEnroll.isEmpty())
            status = EnrollmentAvailabilityStatusTs.ALREADY_ENROLLED

        return enrollmentMapper.userEnrollmentAvailabilityToResponse(user, availableSingleEventsToEnroll, status)
    }

    fun getEnrollmentsForUser(
        userId: Long?,
        startDate: LocalDate,
        endDate: LocalDate
    ): UserEnrolledSingleEventsResponseTs {
        val user = getUserWithPermissionValidation(userId)

        //TODO what to do if old enrollments

        val events =
            enrollmentRepository.findByClientAndBetweenDates(user, startDate.toInstant(), endDate.toInstant())
                .map { eventMapper.singleEventToOccurrenceResponse(it.event) }

        return UserEnrolledSingleEventsResponseTs(events, userMapper.userToUserInfoTs(user))
    }

    fun getActivitiesForUser(showEventsFromPast: Boolean): UserEnrolledActivityResponseTs {
        val user = getUserWithPermissionValidation(null)
        val children = userService.getChildren()
        val currentDate = Instant.now()

        val userActivities: List<ActivityForUserTs>
        val childrenActivities: List<ActivityForUserTs>

        if (showEventsFromPast) {
            userActivities = combineEventsWithUserInfo(
                enrollmentRepository.findAllEventsByUserId(
                    user.id!!
                ), userMapper.userToUserInfoTs(user)
            )

            childrenActivities = children.flatMap { child ->
                combineEventsWithUserInfo(
                    enrollmentRepository.findAllEventsByUserId(
                        child.id
                    ), child
                )
            }
        } else {
            userActivities = combineEventsWithUserInfo(
                enrollmentRepository.findEventsByUserIdWhichAreAfterDateOrIsNotPaid(
                    user.id!!,
                    currentDate
                ), userMapper.userToUserInfoTs(user)
            ).toMutableList()

            childrenActivities = children.flatMap { child ->
                combineEventsWithUserInfo(
                    enrollmentRepository.findEventsByUserIdWhichAreAfterDateOrIsNotPaid(
                        child.id,
                        currentDate
                    ), child
                )
            }.toMutableList()
        }

        return UserEnrolledActivityResponseTs((userActivities + childrenActivities).sortedBy { it.startDate })
    }

    fun deleteUserEnrollmentForActivities(eventId: Long, userId: Long?, numberOfEventsToDelete: Int?) {
        val user = getUserWithPermissionValidation(userId)

        val event = eventRepository.findById(eventId)
            .orElseThrow { ObjectNotFoundException("Event with id $eventId doesn't exist.") }

        val currentDate = Instant.now()

        val pageable = PageRequest.of(0, numberOfEventsToDelete ?: Int.MAX_VALUE, Sort.by(Sort.Direction.DESC, "event.starts"))

        val futureEnrolmentsToDelete =
            enrollmentRepository.findLastFutureClientEnrollments(
                user.id!!,
                event,
                currentDate,
                pageable
            )

        deleteEnrollmentsAndNotifyUsers(futureEnrolmentsToDelete, event, user)
    }

    fun getAllParticipantsOfActivity(eventId: Long, takeFromPast: Boolean): EnrolmentForEventResponseTs {
        val user = authService.loggedInUserOrException()

        val event = commonEventService.getEvent(eventId)

        InstructorValidator.validate(user, event)

        val currentDate = Instant.now()

        val enrolledUser: List<User> = if (takeFromPast) {
            enrollmentRepository.getAllEnrolledUserForActivity(event)
        } else {
            enrollmentRepository.getAllEnrolledUserForActivityWhichAreAfterCurrentDate(event, currentDate)
        }

        val result: List<UserEnrolmentWithDebtTs> = enrolledUser.map {
            UserEnrolmentWithDebtTs(
                user = userMapper.userToUserInfoTs(it),
                debt = eventRepository.getDebtOfUserTakingPartInActivitySingleEvents(it.id!!, event, currentDate)
            )
        }

        return EnrolmentForEventResponseTs(result)
    }

    fun deleteUserEnrollmentForEvent(eventId: Long, userId: Long) {
        val event = commonEventService.getEvent(eventId)
        val user = userService.getUserById(userId)

        val currentDate = Instant.now()

        val enrollmentOfUser = enrollmentRepository.getFutureEnrollmentsForEventForUser(userId, event, currentDate)

        deleteEnrollmentsAndNotifyUsers(enrollmentOfUser, event, user)
    }

    private fun getUserWithPermissionValidation(userId: Long?): User {
        val loggedInUser = authService.loggedInUserOrException().let {
            EnrollmentValidator.hasUserPermissionToEnroll(it, userId ?: it.id!!)
        }

        return if (userId != null && loggedInUser.id != userId)
            userService.getUserById(userId)
        else
            loggedInUser
    }

    private fun combineEventsWithUserInfo(
        events: List<Event>,
        user: UserInfoTs
    ): List<ActivityForUserTs> {
        val currentTime = Instant.now();
        return events.map {
            ActivityForUserTs(
                user = user,
                activity = eventMapper.activityToSummaryTs(it, ""),
                startDate = enrollmentRepository.findMinStartDateFromEvent(user.id, it),
                endDate = enrollmentRepository.findMaxEndDateFromEvent(user.id, it),
                numberOfEnrolledSingleEvents = enrollmentRepository.getNumberOfEnrollmentsForEventForUser(
                    user.id,
                    it,
                    currentTime
                ),
                debtForPastEvents = eventRepository.getDebtOfUserTakingPartInActivitySingleEvents(
                    user.id,
                    it,
                    currentTime
                )
            )
        }
    }

    private fun deleteEnrollmentsAndNotifyUsers(enrollments: List<Enrollment>, event: Event, user: User){
        val moneyToReturn = enrollments.filter {
            it.payment?.status == PaymentStatus.COMPLETED
        }.sumOf {
            it.event.event.cost ?: 0.0
        }

        enrollmentRepository.deleteAll(enrollments)
        if(moneyToReturn > 0.0)
            sendEmailsAfterDisenrollment(event, user, moneyToReturn)
        else
            sendEmailsAfterDisenrollmentNoCost(event, user)
    }

    fun sendEmailsAfterDisenrollment(event: Event, user: User, moneyToReturn: Double){
        val houseOfCultureMail = contactInfoService.getContactInfo().email!!
        val subject = "Zwrot pieniedzy za wydarzenie ${event.title}"
        this.mailSender.sendMailWithTemplate(
            subject = subject,
            targetEmail = houseOfCultureMail,
            templateKey = "disenrollment.classes.house-of-culture",
            "${user.lastName} ${user.firstName} (ID: ${user.id})",
            event.title,
            moneyToReturn.toString()
        )
        this.mailSender.sendMailWithTemplate(
            subject = subject,
            targetEmail = user.email,
            templateKey = "disenrollment.classes.user",
            event.title,
            moneyToReturn.toString()
        )
    }

    private fun sendEmailsAfterDisenrollmentNoCost(event: Event, user: User){
        val subject = "Wypisanie z ${event.title}"

        this.mailSender.sendMailWithTemplate(
            subject = subject,
            targetEmail = user.email,
            templateKey = "disenrollment.classes.user.no-cost",
            event.title,
        )
    }
}
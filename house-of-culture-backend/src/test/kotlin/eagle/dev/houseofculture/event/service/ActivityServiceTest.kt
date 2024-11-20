package eagle.dev.houseofculture.event.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.category.service.CategoryService
import eagle.dev.houseofculture.category.util.CategoryMapper
import eagle.dev.houseofculture.enrollment.repository.EnrollmentRepository
import eagle.dev.houseofculture.enrollment.service.EnrollmentService
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.EventStatus
import eagle.dev.houseofculture.event.model.EventType
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.event.repository.SingleEventRepository
import eagle.dev.houseofculture.event.util.EventMapper
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.image.service.ImageService
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import eagle.dev.houseofculture.user.repository.UserRepository
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

class ActivityServiceTest {
    @RelaxedMockK
    private lateinit var activityService: ActivityService
    private val eventRepository: EventRepository = mockk()
    private val singleEventRepository: SingleEventRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val categoryService: CategoryService = mockk()
    private val eventMapper: EventMapper = mockk()
    private val categoryMapper: CategoryMapper = mockk()
    private val imageService: ImageService = mockk()
    private val commonEventService: CommonEventService = mockk()
    private val authService: AuthService = mockk()
    private val enrollmentService: EnrollmentService = mockk()
    private val enrollmentRepository: EnrollmentRepository = mockk()

    private val TEST_ID: Long = 1L
    private val TEST_VALUE: String = "TEST"
    private val REPEAT: Int = 1
    private val TEST_IMAGE: String = "image1.jpg"

    private val category = Category(
        name = TEST_VALUE
    )

    private val event = Event(
        title = TEST_VALUE,
        type = EventType.MULTIPLE,
        description = TEST_VALUE
    )

    private val singleEvent = SingleEvent(
        event = event,
        starts = Instant.now(),
        ends = Instant.now()
    )

    private val instructor = Client(
        id = TEST_ID,
        password = TEST_VALUE,
        role = UserRole.INSTRUCTOR,
        status = UserStatus.ACTIVE,
        email = TEST_VALUE,
        firstName = TEST_VALUE,
        lastName = TEST_VALUE,
        birthdate = LocalDate.now()
    )

    private val singleEventOccurenceTs = SingleEventOccurenceTs(
        id = TEST_ID,
        mainEventId = TEST_ID,
        startTime = OffsetDateTime.now(),
        endTime = OffsetDateTime.now(),
        title = TEST_VALUE,
        isCancelled = false
    )

    private val categoryResponseTs = CategoryResponseTs(
        id = TEST_ID,
        name = TEST_VALUE
    )

    private val activitySummaryResponseTs = ActivitySummaryResponseTs(
        id = TEST_ID,
        title = TEST_VALUE,
        startDate = LocalDate.now(),
        endDate = LocalDate.now(),
        instructorId = TEST_ID,
        instructorName = TEST_VALUE,
        image = TEST_IMAGE
    )

    private val createSingleEventOccurenceTs = CreateSingleEventOccurenceTs(
        startTime = OffsetDateTime.now(),
        endTime = OffsetDateTime.now(),
        repeat = REPEAT,
        frequency = CreateSingleEventOccurenceTs.Frequency.SINGLE
    )

    private val createActivityRequestTs = CreateActivityRequestTs(
        title = TEST_VALUE,
        occurences = listOf(createSingleEventOccurenceTs),
        instructorId = TEST_ID,
        categories = listOf(TEST_ID),
        images = listOf(TEST_IMAGE)
    )

    @BeforeEach
    fun setUp() {
        activityService = ActivityService(
            eventRepository,
            singleEventRepository,
            userRepository,
            categoryService,
            eventMapper,
            categoryMapper,
            imageService,
            commonEventService,
            authService,
            enrollmentService,
            enrollmentRepository
        )

        event.id = TEST_ID
    }

    @Nested
    inner class CreateActivity {
        @Test
        fun `createActivity should save activity`() {
            // Given
            every { userRepository.findById(createActivityRequestTs.instructorId) } returns Optional.of(instructor)
            every { categoryService.findByIds(any()) } returns listOf(category)
            every { eventRepository.save(any()) } returns event
            every { imageService.saveImage(any(), any(), any()) } returns TEST_IMAGE
            every { imageService.clearDirectory(any()) } just runs
            every { eventRepository.findAllNoCanceledSingleEventsByInstructor(instructor) } returns listOf(singleEvent)

            // When
            val result = activityService.createActivity(createActivityRequestTs)

            // Then
            assertEquals(TEST_ID, result)
            verify {
                eventRepository.save(withArg {
                    assertEquals(createActivityRequestTs.title, it.title)
                    assertEquals(createActivityRequestTs.description, it.description)
                    assertEquals(listOf(category), it.categories)
                    assertEquals(instructor, it.instructor)
                    assertEquals(createActivityRequestTs.minimumAge, it.minimumAge)
                    assertEquals(createActivityRequestTs.maximumAge, it.maximumAge)
                    assertEquals(createActivityRequestTs.maxPlaces, it.maxPlaces)
                    assertEquals(createActivityRequestTs.cost, it.cost)
                })
            }
        }

        @Test
        fun `createActivity should throw UnprocessableEntityException while user not found`() {
            // Given
            every { userRepository.findById(createActivityRequestTs.instructorId) } returns Optional.empty()

            // When/Then
            assertThrows<ObjectNotFoundException> {
                activityService.createActivity(createActivityRequestTs)
            }
            verify(exactly = 0) {
                eventRepository.save(any())
                categoryService.findByIds(any())
                imageService.saveImage(any(), any(), any())
                imageService.clearDirectory(any())
                eventRepository.findAllNoCanceledSingleEventsByInstructor(any())
            }
        }
    }

    @Nested
    inner class GetActivity {

        val activityResponseTs = ActivityResponseTs(
            id = TEST_ID,
            title = TEST_VALUE,
            occurences = listOf(singleEventOccurenceTs),
            instructorId = TEST_ID,
            instructorName = TEST_VALUE,
            description = TEST_VALUE,
            categories = listOf(categoryResponseTs),
            images = listOf(TEST_IMAGE)
        )

        @Test
        fun `getActivity should return ActivityResponseTs`() {
            // Given
            every { commonEventService.getEvent(TEST_ID) } returns event
            every { imageService.getAllImages(any()) } returns listOf(TEST_IMAGE)
            every { eventMapper.activityToTs(event, listOf(TEST_IMAGE)) } returns activityResponseTs

            // When
            val result = activityService.getActivity(TEST_ID)

            // Then
            verifyOrder {
                commonEventService.getEvent(TEST_ID)
                eventMapper.activityToTs(event, eq(listOf(TEST_IMAGE)))
            }
            assertNotNull(result)
        }

        @Test
        fun `getActivity should throw ObjectNotFoundException while event not found`() {
            // Given
            every { commonEventService.getEvent(TEST_ID) } throws ObjectNotFoundException("")

            // When/Then
            assertThrows<ObjectNotFoundException> {
                activityService.getActivity(TEST_ID)
            }

            verify(exactly = 0) {
                eventMapper.activityToTs(event, listOf(TEST_IMAGE))
                imageService.getAllImages(any())
            }
        }
    }

    @Nested
    inner class GetActivitiesCategories {
        @Test
        fun `getActivitiesCategories should return categories with specified number`() {
            // Given
            val categories = listOf(category)

            every {
                eventRepository.findMostPopularAssociatedCategories(
                    EventType.MULTIPLE,
                    PageRequest.of(0, 1),
                    any()
                )
            } returns categories
            every { categoryMapper.categoryToTs(category) } returns categoryResponseTs

            // When
            val result = activityService.getActivitiesCategories(1)

            // Then
            assertEquals(categories.size, result.size)
            assertEquals(categories.map { it.name }, result.map { it.name })
        }

        @Test
        fun `getActivitiesCategories should return all categories when number is null`() {
            // Given
            val categories = listOf(category)

            every {
                eventRepository.findMostPopularAssociatedCategories(
                    EventType.MULTIPLE,
                    null,
                    any()
                )
            } returns categories
            every { categoryMapper.categoryToTs(category) } returns categoryResponseTs

            // When
            val result = activityService.getActivitiesCategories(null)

            // Then
            assertEquals(categories.size, result.size)
            assertEquals(categories.map { it.name }, result.map { it.name })
        }

    }

    @Nested
    inner class GetPopularActivities {
        @Test
        fun `getPopularActivities should return PopularActivitiesResponseTs with expected data`() {
            // Given
            val categories = listOf(category)

            val eventsUncategorized = listOf(event)
            val eventsCategorized = listOf(event)

            every {
                eventRepository.findMostPopularAssociatedCategories(
                    EventType.MULTIPLE,
                    null,
                    any()
                )
            } returns categories
            every { eventRepository.findTop4By(any()) } returns eventsUncategorized
            every { eventRepository.findTop4ByCategoriesContaining(categories[0], any()) } returns eventsCategorized
            every { eventMapper.activityToSummaryTs(event, TEST_IMAGE) } returns activitySummaryResponseTs
            every { imageService.getFirstImage(any()) } returns TEST_IMAGE

            // When
            val result = activityService.getPopularActivities()

            // Then
            assertEquals(1, result.main.size)
            assertEquals(1, result.categorized.size)

            // Verify the structure of the response
            assertEquals(TEST_VALUE, result.main[0].title)
            assertEquals(TEST_IMAGE, result.main[0].image)

            assertEquals(TEST_VALUE, result.categorized[0].categoryName)
            result.categorized[0].activities?.let { assertEquals(1, it.size) }

            verify(exactly = 1) { eventRepository.findMostPopularAssociatedCategories(EventType.MULTIPLE, null, any()) }
            verify(exactly = 1) { eventRepository.findTop4By(any()) }
            verify(exactly = 1) { eventRepository.findTop4ByCategoriesContaining(category, any()) }
            verify(exactly = 2) { imageService.getFirstImage(any()) }
        }
    }

    @Nested
    inner class GetActivities {
        @Test
        fun `getActivities should return FilteredActivitiesResponseTs with expected data`() {
            // Given
            val page = 0
            val pageSize = 1
            val pageRequest = PageRequest.of(page, pageSize)

            every { categoryService.findByIdOrNull(TEST_ID) } returns category
            every { eventRepository.findAllByTitleContainingAndCategoriesContainingAndStatus(TEST_VALUE, category, EventStatus.ACTIVE, PageRequest.of(page, pageSize)) } returns PageImpl(listOf(event))
            every { eventRepository.findAllByTitleContainingAndStatus(TEST_VALUE, EventStatus.ACTIVE, pageRequest) } returns PageImpl(listOf(event))
            every { imageService.getFirstImage(any()) } returns TEST_IMAGE
            every { eventMapper.activityToSummaryTs(event, TEST_IMAGE) } returns activitySummaryResponseTs

            // When
            val result = activityService.getActivities(page, pageSize, TEST_VALUE, TEST_ID)

            // Then
            assertEquals(1, result.items.size)
            assertEquals(TEST_VALUE, result.items[0].title)
            assertEquals(TEST_IMAGE, result.items[0].image)

            verify(exactly = 1) { categoryService.findByIdOrNull(TEST_ID) }
            verify(exactly = 1) { eventRepository.findAllByTitleContainingAndCategoriesContainingAndStatus(TEST_VALUE, category,EventStatus.ACTIVE, pageRequest) }
            verify(exactly = 0) { eventRepository.findAllByTitleContainingAndStatus(TEST_VALUE,EventStatus.ACTIVE, pageRequest) }
            verify(exactly = 1) { imageService.getFirstImage(any()) }
        }
    }

    @Nested
    inner class EditActivity {
        @Test
        fun `editActivity should update activity and return activity id`() {
            // Given
            val editRequest = EditActivityRequestTs(
                title = "Updated Title",
                description = "Updated Description",
                instructorId = TEST_ID,
                categories = listOf(TEST_ID),
                minimumAge = 10,
                maximumAge = 15,
                maxPlaces = 20,
                cost = 30.0,
                occurences = emptyList(),
                images = emptyList()
            )

            every { eventRepository.findById(TEST_ID) } returns Optional.of(event)
            every { userRepository.findById(createActivityRequestTs.instructorId) } returns Optional.of(instructor)
            every { categoryService.findByIds(any()) } returns listOf(category)
            every { eventRepository.save(event) } returns event
            every { imageService.clearDirectory(any()) } just runs

            // When
            val result = activityService.editActivity(TEST_ID, editRequest)

            // Then
            assertEquals(TEST_ID, result)

            // Verify that the activity properties are updated
            assertEquals("Updated Title", event.title)
            assertEquals("Updated Description", event.description)
            assertEquals(10, event.minimumAge)
            assertEquals(15, event.maximumAge)
            assertEquals(20, event.maxPlaces)
            assertEquals(30.0, event.cost)
            // Verify that updateSingleEvents is called if needed

            // Verify that getInstructor and getCategories are called with the correct parameters
            verify { userRepository.findById(createActivityRequestTs.instructorId) }
            verify { categoryService.findByIds(any()) }

        }
    }

    @Nested
    inner class DeleteActivity {
        @Test
        fun `deleteActivity should cancel activity if available places are not equal to max places`() {
            // Given
            every { eventRepository.findById(TEST_ID) } returns Optional.of(event)
            every { imageService.clearDirectory(any()) } just runs
            every { eventRepository.save(any()) } returns event
            every { eventRepository.getEnrollmentsForEvent(event) } returns emptyList()
            every { eventRepository.delete(event) } just runs

            // When
            activityService.deleteActivity(TEST_ID)

            // Then
            verify(exactly = 1) { eventRepository.delete(event) }
        }
    }

    @Nested
    inner class GetInstructorActivity {
        @Test
        fun `getInstructorActivity should return list of ActivitySummaryResponseTs`() {
            // Given
            every { authService.loggedInUserOrException() } returns instructor
            every { eventRepository.getAllCurrentlyOngoingActivitiesForInstructor(instructor, any()) } returns listOf(event)
            every { eventMapper.activityToSummaryTs(event, "") } returns activitySummaryResponseTs

            // When
            val result = activityService.getInstructorActivity()

            // Then
            assertEquals(1, result.size)
            assertEquals(TEST_ID, result[0].id)
            assertEquals(TEST_VALUE, result[0].title)

            verify(exactly = 1) { authService.loggedInUserOrException() }
            verify(exactly = 1) { eventRepository.getAllCurrentlyOngoingActivitiesForInstructor(instructor, any()) }
            verify(exactly = 1) { eventMapper.activityToSummaryTs(event, "") }
        }


        @Test
        fun `getInstructorActivity should throw an exception if loggedInUserOrException() fails`() {
            // Given
            every { authService.loggedInUserOrException() } throws NoSuchElementException("User not found")

            // When/Then
            assertThrows<NoSuchElementException> {
                activityService.getInstructorActivity()
            }

            verify(exactly = 1) { authService.loggedInUserOrException() }
            verify(exactly = 0) { eventRepository.getAllCurrentlyOngoingActivitiesForInstructor(any(), any()) }
            verify(exactly = 0) { eventMapper.activityToSummaryTs(any(), any()) }
        }
    }
}
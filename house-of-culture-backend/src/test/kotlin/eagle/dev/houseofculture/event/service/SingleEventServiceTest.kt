package eagle.dev.houseofculture.event.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.contact.service.ContactInfoService
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.EventStatus
import eagle.dev.houseofculture.event.model.EventType
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.event.repository.SingleEventRepository
import eagle.dev.houseofculture.event.util.EventMapper
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.mail.MailSender
import eagle.dev.houseofculture.openapi.model.ContactInfoTs
import eagle.dev.houseofculture.openapi.model.EditSingleEventRequestTs
import eagle.dev.houseofculture.openapi.model.SendEmailRequestTs
import eagle.dev.houseofculture.openapi.model.SingleEventOccurenceTs
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import eagle.dev.houseofculture.user.service.UserService
import eagle.dev.houseofculture.user.validator.UserValidator
import eagle.dev.houseofculture.util.toFormattedOffsetDateTime
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

class SingleEventServiceTest {
    private lateinit var singleEventService: SingleEventService
    private val singleEventRepository: SingleEventRepository = mockk()
    private val authService: AuthService = mockk()
    private val userValidator: UserValidator = mockk()
    private val eventMapper: EventMapper = mockk()
    private val mailSender: MailSender = mockk()
    private val eventRepository: EventRepository = mockk()
    private val contactInfoService: ContactInfoService = mockk()
    private val userService: UserService = mockk()

    private val TEST_ID: Long = 1L
    private val TEST_VALUE: String = "TEST"

    private val editSingleEventRequestTs = EditSingleEventRequestTs(
        id = TEST_ID,
        startTime = OffsetDateTime.now().plusDays(1).minusHours(1),
        endTime = OffsetDateTime.now().plusDays(1),
        isCanceled = false
    )

    private val singleEventOccurenceTs = SingleEventOccurenceTs(
        id = TEST_ID,
        mainEventId = TEST_ID,
        startTime = OffsetDateTime.now(),
        endTime = OffsetDateTime.now().minusHours(1),
        title = TEST_VALUE,
        isCancelled = false
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

    private val event = Event(
        title = TEST_VALUE,
        type = EventType.MULTIPLE,
        description = TEST_VALUE,
        instructor = instructor
    )

    private val singleEvent = SingleEvent(
        starts = Instant.now(),
        ends = Instant.now().plusSeconds(3600),
        isCancelled = true,
        event = event
    )

    @BeforeEach
    fun setUp() {
        singleEventService = SingleEventService(
            singleEventRepository,
            authService,
            userValidator,
            eventMapper,
            mailSender,
            eventRepository,
            contactInfoService,
            userService
        )

        singleEvent.id = TEST_ID
    }

    @Nested
    inner class EditSingleEvent {
        @Test
        fun `editSingleEvent should update the single event`() {
            // Given
            every { singleEventRepository.findById(TEST_ID) } returns Optional.of(singleEvent)
            every { singleEventRepository.save(singleEvent) } returns singleEvent
            every { eventRepository.findAllNoCanceledSingleEventsByInstructorNotForEvent(instructor, event) } returns listOf()
            // When
            val result = singleEventService.editSingleEvent(editSingleEventRequestTs)

            // Then
            assertEquals(TEST_ID, result)
            verify {
                singleEventRepository.findById(TEST_ID)
                singleEventRepository.save(withArg {
                    assertEquals(editSingleEventRequestTs.startTime.toInstant(), it.starts)
                    assertEquals(editSingleEventRequestTs.endTime.toInstant(), it.ends)
                    assertEquals(editSingleEventRequestTs.isCanceled, it.isCancelled)
                })
            }
        }

        @Test
        fun `editSingleEvent should throw ObjectNotFoundException while event not found`() {
            // Given
            every { singleEventRepository.findById(TEST_ID) } returns Optional.empty()

            // When/Then
            assertThrows<ObjectNotFoundException> {
                singleEventService.editSingleEvent(editSingleEventRequestTs)
            }

            verify(exactly = 0) { singleEventRepository.save(any()) }
        }
    }

    @Nested
    inner class FindEventsByInstructorAndBetween {

        @Test
        fun `findEventsByInstructorAndBetween should return list of SingleEventOccurenceTs`() {
            // Given
            val startDate = LocalDate.of(2023, 1, 1)
            val endDate = LocalDate.of(2023, 1, 31)
            val singleEvents = listOf(singleEvent)

            every { authService.loggedInUserOrException() } returns instructor
            every {
                singleEventRepository.findByInstructorAndBetween(
                    instructor,
                    any<Instant>(),
                    any<Instant>(),
                    EventStatus.ACTIVE
                )
            } returns singleEvents
            every { userValidator.isRoleAllowed(UserRole.INSTRUCTOR, UserRole.INSTRUCTOR) } just runs
            every { eventMapper.singleEventToOccurrenceResponse(singleEvent) } returns singleEventOccurenceTs

            // When
            val result = singleEventService.findEventsByInstructorAndBetween(startDate, endDate)

            // Then
            assertEquals(singleEvents.size, result.size)
            assertEquals(
                singleEvents.map { eventMapper.singleEventToOccurrenceResponse(it) },
                result
            )
        }
    }
    @Nested
    inner class SendEmailToEnrolledUsers {
        @Test
        fun `sendEmailToEnrolledUsers should send email to enrolled users and instructor`() {
            // Given
            val singleEventId = TEST_ID
            val sendEmailRequestTs = SendEmailRequestTs(
                subject = TEST_VALUE,
                content = TEST_VALUE
            )

            every { authService.loggedInUserOrException() } returns instructor
            every { singleEventRepository.findById(any()) } returns Optional.of(singleEvent)
            every { eventMapper.singleEventToOccurrenceResponse(singleEvent) } returns singleEventOccurenceTs
            every { userValidator.isRoleAllowed(UserRole.INSTRUCTOR, UserRole.INSTRUCTOR) } just runs

            val content = getSummaryOfEvent(singleEvent) + "\n\n" + sendEmailRequestTs.content

            every { mailSender.sendMail(sendEmailRequestTs.subject, any<Array<String>>(), content) } just runs


            // When
            singleEventService.sendEmailToEnrolledUsers(singleEventId, sendEmailRequestTs)

            // Then
            verify {
                mailSender.sendMail(
                    subject = sendEmailRequestTs.subject,
                    targetEmail = any<Array<String>>(),
                    text = content
                )
            }
        }

        private fun getSummaryOfEvent(singleEvent: SingleEvent): String {
            val event = singleEvent.event
            return "[${singleEvent.starts.toFormattedOffsetDateTime()}] - [${singleEvent.ends.toFormattedOffsetDateTime()}] ${event.title}"
        }
    }

    @Nested
    inner class GetUserICSCalendar {
        @Test
        fun `getUserICSCalendar should return Resource with events`() {
            // Given
            val startRange = LocalDate.of(2023, 1, 1)
            val endRange = LocalDate.of(2023, 1, 31)
            val contactInfo = ContactInfoTs(
                institutionName = TEST_VALUE
            )

            every { authService.loggedInUserOrException() } returns instructor
            every { singleEventRepository.findByInstructorAndBetween(instructor, any(), any(), EventStatus.ACTIVE) } returns listOf(singleEvent)
            every { contactInfoService.getContactInfo() } returns contactInfo

            // When
            val result = singleEventService.getUserICSCalendar(startRange, endRange, true, TEST_ID)

            // Then
            assertNotNull(result)
        }
    }
}
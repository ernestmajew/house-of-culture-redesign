package eagle.dev.houseofculture.event.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.contact.service.ContactInfoService
import eagle.dev.houseofculture.event.model.EventStatus
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.event.repository.SingleEventRepository
import eagle.dev.houseofculture.event.util.EventMapper
import eagle.dev.houseofculture.event.util.validator.SingleEventValidator
import eagle.dev.houseofculture.exceptions.ForbiddenException
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.mail.MailSender
import eagle.dev.houseofculture.openapi.model.EditSingleEventRequestTs
import eagle.dev.houseofculture.openapi.model.SendEmailRequestTs
import eagle.dev.houseofculture.openapi.model.SingleEventOccurenceTs
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.service.UserService
import eagle.dev.houseofculture.user.validator.UserValidator
import eagle.dev.houseofculture.util.CalendarExportUtil
import eagle.dev.houseofculture.util.toFormattedOffsetDateTime
import eagle.dev.houseofculture.util.toInstant
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SingleEventService(
    private val singleEventRepository: SingleEventRepository,
    private val authService: AuthService,
    private val userValidator: UserValidator,
    private val eventMapper: EventMapper,
    private val mailSender: MailSender,
    private val eventRepository: EventRepository,
    private val contactInfoService: ContactInfoService,
    private val userService: UserService
) {
    fun editSingleEvent(editSingleEventRequestTs: EditSingleEventRequestTs): Long {
        val id = editSingleEventRequestTs.id
        val singleEvent = singleEventRepository.findById(id).orElseThrow {
            throw ObjectNotFoundException("Single event with id $id doesn't exist.")
        }

        SingleEventValidator.validate(editSingleEventRequestTs)

        with(editSingleEventRequestTs){
            singleEvent.starts = editSingleEventRequestTs.startTime.toInstant()
            singleEvent.ends = editSingleEventRequestTs.endTime.toInstant()
            singleEvent.isCancelled = editSingleEventRequestTs.isCanceled!!
        }

        val event = singleEvent.event
        val instructorActivities =
            event.instructor?.let { eventRepository.findAllNoCanceledSingleEventsByInstructorNotForEvent(it, event) }
                ?: emptyList()

        SingleEventValidator.checkCollisions(event, singleEvent, instructorActivities)

        return singleEventRepository.save(singleEvent).id!!
    }

    fun findEventsByInstructorAndBetween(startDate: LocalDate, endDate: LocalDate): List<SingleEventOccurenceTs> {
        val instructor = getLoggedInstructor()

        return singleEventRepository.findByInstructorAndBetween(
            instructor, startDate.toInstant(), endDate.toInstant(), EventStatus.ACTIVE)
            .map { eventMapper.singleEventToOccurrenceResponse(it) }

    }


    // it is async, so we don't even know if it was sent or host is down
    fun sendEmailToEnrolledUsers(singleEventId: Long, sendEmailRequestTs: SendEmailRequestTs){
        val instructor = getLoggedInstructor()
        val singleEvent = findById(singleEventId)
        val event = singleEvent.event

        if (event.instructor != instructor) {
            throw ForbiddenException("You are not instructor of this event.")
        }

        val summaryOfEvent = getSummaryOfEvent(singleEvent)
        val enrolledUserMails = singleEvent.enrollments.map { it.client.email }.toMutableList()
        enrolledUserMails.add(instructor.email)

        val content = summaryOfEvent + "\n\n" + sendEmailRequestTs.content
        mailSender.sendMail(sendEmailRequestTs.subject,
            enrolledUserMails.toTypedArray(),
            content
        )
    }

    fun getUserICSCalendar(startRange: LocalDate, endRange: LocalDate, isInstructor: Boolean=false, userId: Long?): Resource {
        var user = authService.loggedInUserOrException()
        if (userId != null && user.id != userId) {
            userValidator.validateIsUserChild(user,  userService.getUserById(userId))
            user = userService.getUserById(userId)
        }

        val singleEvents = if (isInstructor) {
            singleEventRepository.findByInstructorAndBetween(
                user as Client,
                startRange.toInstant(),
                endRange.toInstant(),
                EventStatus.ACTIVE
            )
        } else {
            singleEventRepository.findByClientAndBetweenDates(
                user,
                startRange.toInstant(),
                endRange.toInstant(),
                EventStatus.ACTIVE
            )
        }

        if(singleEvents.isEmpty())
            throw UnprocessableEntityException("No events found for user ${user.id} in range $startRange - $endRange")

        return CalendarExportUtil.getICSCalendar(singleEvents, this.contactInfoService.getContactInfo())
    }

    private fun findById(id: Long) = singleEventRepository.findById(id).orElseThrow {
        throw ObjectNotFoundException("Single event with id $id doesn't exist.")
    }

    private fun getSummaryOfEvent(singleEvent: SingleEvent): String {
        val event = singleEvent.event
        return "[${singleEvent.starts.toFormattedOffsetDateTime()}] - [${singleEvent.ends.toFormattedOffsetDateTime()}] ${event.title}"
    }
    private fun getLoggedInstructor(): Client {
        val loggedInUser = authService.loggedInUserOrException()
        userValidator.isRoleAllowed(loggedInUser.role, UserRole.INSTRUCTOR)
        return loggedInUser as Client
    }
}
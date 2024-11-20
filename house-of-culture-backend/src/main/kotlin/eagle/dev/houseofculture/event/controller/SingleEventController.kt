package eagle.dev.houseofculture.event.controller

import eagle.dev.houseofculture.event.service.SingleEventService
import eagle.dev.houseofculture.openapi.api.SingleEventApi
import eagle.dev.houseofculture.openapi.model.EditSingleEventRequestTs
import eagle.dev.houseofculture.openapi.model.SendEmailRequestTs
import eagle.dev.houseofculture.openapi.model.SingleEventOccurenceTs
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Transactional
@RestController
class SingleEventController(
    private val singleEventService: SingleEventService
) : SingleEventApi {
    override fun editSingleEvent(editSingleEventRequestTs: EditSingleEventRequestTs): ResponseEntity<Long> {
        return singleEventService.editSingleEvent(editSingleEventRequestTs).let { ResponseEntity.ok(it) }
    }

    override fun getInstructorSingleEvents(
        startDate: LocalDate,
        endDate: LocalDate,
    ): ResponseEntity<List<SingleEventOccurenceTs>> =
        ResponseEntity.ok(singleEventService.findEventsByInstructorAndBetween(startDate, endDate))

    override fun sendEmailToEnrolledUsers(
        singleEventId: Long,
        sendEmailRequestTs: SendEmailRequestTs
    ): ResponseEntity<Unit> {
        singleEventService.sendEmailToEnrolledUsers(singleEventId, sendEmailRequestTs)
        return ResponseEntity.noContent().build()
    }

    override fun getICSCalendar(
        startDate: LocalDate,
        endDate: LocalDate,
        userId: Long?,
        instructorCalendar: Boolean
    ): ResponseEntity<Resource> {
        return singleEventService.getUserICSCalendar(startDate, endDate, instructorCalendar, userId)
            .let { ResponseEntity.ok(it) }
    }
}
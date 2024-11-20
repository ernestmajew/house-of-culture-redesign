package eagle.dev.houseofculture.enrollment.controller

import eagle.dev.houseofculture.enrollment.service.EnrollmentService
import eagle.dev.houseofculture.openapi.api.EnrollmentApi
import eagle.dev.houseofculture.openapi.model.*
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Transactional
class EnrollmentController(
    private val enrollmentService: EnrollmentService
): EnrollmentApi {
    override fun enrollToEvent(
        eventId: Long,
        createEnrollmentRequestTs: CreateEnrollmentRequestTs
    ): ResponseEntity<List<SingleEventOccurenceTs>> =
        enrollmentService
            .enrollToEvent(eventId, createEnrollmentRequestTs)
            .let { ResponseEntity.ok(it) }

    override fun getEnrollmentAvailability(
        @Parameter(
            description = "Id of event/activity to enroll",
            required = true
        ) @PathVariable(value = "eventId") eventId: Long
    ): ResponseEntity<EnrollmentAvailabilityResponseTs> =
        enrollmentService
            .getEnrollmentAvailability(eventId)
            .let { ResponseEntity.ok(it) }

    override fun getEnrollments(
        startDate: LocalDate,
        endDate: LocalDate,
        userId: Long?
    ): ResponseEntity<UserEnrolledSingleEventsResponseTs> {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsForUser(userId, startDate, endDate))
    }

    override fun getAllActivitiesEnrollmentForUser(
        showEventsFromPast: Boolean
    ): ResponseEntity<UserEnrolledActivityResponseTs> {
        return ResponseEntity.ok(enrollmentService.getActivitiesForUser(showEventsFromPast))
    }

    override fun deleteUserEnrollmentForActivities(
        eventId: Long,
        userId: Long?,
        numberOfEventsToDelete: Int?
    ): ResponseEntity<Unit> {
        enrollmentService.deleteUserEnrollmentForActivities(eventId, userId, numberOfEventsToDelete)
        return ResponseEntity.noContent().build()
    }

    override fun getAllParticipantsOfActivity(
        eventId: Long,
        takeFromPast: Boolean
    ): ResponseEntity<EnrolmentForEventResponseTs> {
        return ResponseEntity.ok(enrollmentService.getAllParticipantsOfActivity(eventId, takeFromPast))
    }

    override fun deleteUserEnrollmentForEvent(eventId: Long, userId: Long): ResponseEntity<Unit> {
        enrollmentService.deleteUserEnrollmentForEvent(eventId, userId)
        return ResponseEntity.noContent().build()
    }
}
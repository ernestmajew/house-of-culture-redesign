package eagle.dev.houseofculture.enrollment.controller

import eagle.dev.houseofculture.enrollment.service.EnrollmentPaymentService
import eagle.dev.houseofculture.openapi.api.EnrollmentPaymentApi
import eagle.dev.houseofculture.openapi.model.EnrollmentPaymentsResponseTs
import eagle.dev.houseofculture.openapi.model.PeriodicPaymentInvoiceTs
import eagle.dev.houseofculture.openapi.model.RedirectUriResponseTs
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Transactional
class EnrollmentPaymentController(
    private val enrollmentPaymentService: EnrollmentPaymentService
) : EnrollmentPaymentApi {
    override fun getEnrollmentsPayments(eventId: Long, userId: Long?): ResponseEntity<EnrollmentPaymentsResponseTs> =
        enrollmentPaymentService
            .getEnrollmentsPayments(eventId, userId)
            .let { ResponseEntity.ok(it) }

    override fun payForEnrollments(
        eventId: Long,
        userId: Long?,
        numberOfEnrollments: Int?
    ): ResponseEntity<RedirectUriResponseTs> =
        enrollmentPaymentService
            .payForEnrollments(eventId, userId, numberOfEnrollments)
            .let { ResponseEntity.ok(it) }

    override fun getPeriodicPaymentInvoice(
        usersIds: List<Long>,
        start: LocalDate,
        end: LocalDate
    ): ResponseEntity<PeriodicPaymentInvoiceTs> =
        enrollmentPaymentService
            .getPeriodicPaymentInvoice(usersIds, start, end)
            .let { ResponseEntity.ok(it) }

    override fun createPeriodicPayment(
        usersIds: List<Long>,
        start: LocalDate,
        end: LocalDate
    ): ResponseEntity<RedirectUriResponseTs> =
        enrollmentPaymentService
            .createPeriodicPayment(usersIds, start, end)
            .let { ResponseEntity.ok(it) }
}
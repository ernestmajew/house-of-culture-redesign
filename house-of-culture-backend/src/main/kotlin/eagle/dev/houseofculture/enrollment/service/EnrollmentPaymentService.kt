package eagle.dev.houseofculture.enrollment.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.enrollment.model.Enrollment
import eagle.dev.houseofculture.enrollment.model.Payment
import eagle.dev.houseofculture.enrollment.repository.EnrollmentRepository
import eagle.dev.houseofculture.enrollment.repository.PaymentRepository
import eagle.dev.houseofculture.enrollment.util.EnrollmentPaymentMapper
import eagle.dev.houseofculture.enrollment.validator.EnrollmentValidator
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.service.CommonEventService
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.openapi.model.EnrollmentPaymentsResponseTs
import eagle.dev.houseofculture.openapi.model.PeriodicPaymentInvoiceTs
import eagle.dev.houseofculture.openapi.model.PaidEnrolmentInfoTs
import eagle.dev.houseofculture.openapi.model.RedirectUriResponseTs
import eagle.dev.houseofculture.payu.service.PayUOrderService
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.service.UserService
import eagle.dev.houseofculture.util.toInstant
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class EnrollmentPaymentService(
    private val authService: AuthService,
    private val commonEventService: CommonEventService,
    private val payUOrderService: PayUOrderService,
    private val userService: UserService,
    private val enrollmentRepository: EnrollmentRepository,
    private val paymentRepository: PaymentRepository,
    private val enrollmentPaymentMapper: EnrollmentPaymentMapper
) {
    fun getEnrollmentsPayments(eventId: Long, userId: Long?): EnrollmentPaymentsResponseTs {
        val user = authService.getLoggedInClientOrHisChild(userId)
        val event = commonEventService.getEvent(eventId)

        isUserEnrolledToEvent(user, event)

        val numberOfEnrollmentsToPay = enrollmentRepository.countUnpaidUserEnrollmentsInEvent(user, event)

        val paidEnrollmentInfoList = enrollmentRepository.getPaidEnrolmentsForEventsOfUser(user, event)
            .groupBy { it.payment }
            .map { (payment, enrollments) ->
                val date = payment?.time!!.toLocalDate()
                val amount = enrollments.sumOf { it.event.event.cost ?: 0.0 }
                val periodicPayment = amount != payment.amount
                PaidEnrolmentInfoTs(
                    date,
                    amount,
                    enrollments.size,
                    PaidEnrolmentInfoTs.Status.valueOf(payment.status.name),
                    periodicPayment
                )
            }

        return enrollmentPaymentMapper.enrollmentPaymentsToResponse(user, event, numberOfEnrollmentsToPay, paidEnrollmentInfoList)
    }

    fun payForEnrollments(eventId: Long, userId: Long?, numberOfEnrollments: Int?): RedirectUriResponseTs {
        val user = authService.getLoggedInClientOrHisChild(userId)
        val event = commonEventService.getEvent(eventId)

        isUserEnrolledToEvent(user, event)

        val pageRequest = PageRequest.of(0, numberOfEnrollments ?: Int.MAX_VALUE)
            .withSort(Sort.by("event.starts"))

        val enrollmentsToPay = enrollmentRepository
            .getUnpaidEnrollmentsInEventOfUser(user, event, pageRequest)
            .also { if (it.isEmpty()) throw UnprocessableEntityException("User has no enrollments to pay.") }

        return createPaymentForEnrollments(enrollmentsToPay)
    }

    fun getPeriodicPaymentInvoice(usersIds: List<Long>, start: LocalDate, end: LocalDate): PeriodicPaymentInvoiceTs {
        val enrollmentsToPay = getEnrollmentsToPeriodicPayment(usersIds, start, end)

        return PeriodicPaymentInvoiceTs(enrollmentsToPay
            .groupBy { it.event.event }
            .map {
                enrollmentPaymentMapper.toPeriodicPaymentInvoiceItemResponse(
                    it.value.first().client,
                    it.key, // event
                    it.value.size // number of unpaid enrollments
                )
            })
    }

    fun createPeriodicPayment(usersIds: List<Long>, start: LocalDate, end: LocalDate): RedirectUriResponseTs =
        createPaymentForEnrollments(getEnrollmentsToPeriodicPayment(usersIds, start, end))

    private fun getEnrollmentsToPeriodicPayment(
        usersIds: List<Long>,
        start: LocalDate,
        end: LocalDate
    ): List<Enrollment> {
        EnrollmentValidator.validatePeriodicPaymentRange(start, end)

        val loggedInUser = authService.loggedInUserOrException()
        EnrollmentValidator.hasUserPermissionToPay(loggedInUser, usersIds)

        val usersToPay = userService.getUsersByIds(usersIds)
        return enrollmentRepository.getUnpaidEnrollmentsInRangeForUsers(
            usersToPay,
            start.toInstant(),
            // hour is set to 00:00, so add 1 day to capture the end day as well
            end.toInstant().plus(1, ChronoUnit.DAYS)
        )
    }

    private fun isUserEnrolledToEvent(user: User, event: Event) {
        if (!enrollmentRepository.existsByClientAndEventEvent(user, event))
            throw ConflictException("User (id: ${user.id}) is not enrolled to event (id: ${event.id})")
    }
    
    // returns redirectUri
    private fun createPaymentForEnrollments(enrollments: List<Enrollment>): RedirectUriResponseTs {
        val buyer: Client = authService
            .loggedInUserOrException()
            .also(EnrollmentValidator::hasUserPermissionToPay)
                as Client

        val totalCost = enrollmentRepository.getTotalCostOfEnrollments(enrollments)

        val payment = Payment(
            totalCost,
            LocalDateTime.now(),
            buyer,
            enrollments.toSet()
        ).let(paymentRepository::save)

        val customerIp = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request.remoteAddr

        return payUOrderService.createOrder(payment, customerIp).let(enrollmentPaymentMapper::redirectUriToResponse)
    }
}
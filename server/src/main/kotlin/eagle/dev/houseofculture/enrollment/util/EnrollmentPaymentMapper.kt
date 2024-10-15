package eagle.dev.houseofculture.enrollment.util

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.openapi.model.EnrollmentPaymentsResponseTs
import eagle.dev.houseofculture.openapi.model.PeriodicPaymentInvoiceItemResponseTs
import eagle.dev.houseofculture.openapi.model.PaidEnrolmentInfoTs
import eagle.dev.houseofculture.openapi.model.RedirectUriResponseTs
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.util.UserMapper
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@JvmDefaultWithCompatibility // needed to use java "public default" scope on methods
@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = [UserMapper::class]
)
interface EnrollmentPaymentMapper {
    fun enrollmentPaymentsToResponse(
        user: User,
        event: Event,
        unpaidEnrollments: Int,
        paidEnrollments: List<PaidEnrolmentInfoTs>
    ): EnrollmentPaymentsResponseTs

    fun toPeriodicPaymentInvoiceItemResponse(
        user: User,
        event: Event,
        unpaidEnrollments: Int
    ): PeriodicPaymentInvoiceItemResponseTs

    fun redirectUriToResponse(uri: String): RedirectUriResponseTs
}
package eagle.dev.houseofculture.enrollment.util

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.event.util.EventMapper
import eagle.dev.houseofculture.openapi.model.EnrollmentAvailabilityResponseTs
import eagle.dev.houseofculture.openapi.model.EnrollmentAvailabilityStatusTs
import eagle.dev.houseofculture.openapi.model.UserEnrollmentAvailabilityResponseTs
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.util.mapper.CommonMapper
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@JvmDefaultWithCompatibility // needed to use java "public default" scope on methods
@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = [EventMapper::class, CommonMapper::class]
)
interface EnrollmentMapper {
    @Mapping(source = "user", target = "userFullName", qualifiedByName = ["getUserFullName"])
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "status", target = "status")
    fun userEnrollmentAvailabilityToResponse(
        user: User,
        singleEvents: List<SingleEvent>,
        status: EnrollmentAvailabilityStatusTs
    ): UserEnrollmentAvailabilityResponseTs


    @Mapping(source = "event.title", target = "title")
    @Mapping(source = "event.cost", target = "cost")
    fun enrollmentAvailabilityToResponse(
        event: Event,
        usersAvailability: List<UserEnrollmentAvailabilityResponseTs>
    ): EnrollmentAvailabilityResponseTs
}
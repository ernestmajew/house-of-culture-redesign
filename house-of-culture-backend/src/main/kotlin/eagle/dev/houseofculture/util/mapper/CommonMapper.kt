package eagle.dev.houseofculture.util.mapper

import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.util.toOffsetDateTimeWithoutZone
import org.mapstruct.Mapper
import org.mapstruct.Named
import java.time.Instant
import java.time.OffsetDateTime

@Mapper(componentModel = "spring")
@JvmDefaultWithCompatibility // needed to use java "public default" scope on methods
interface CommonMapper {
    // transform using local timeZone
    @Named("instantToOffsetDateTime")
    fun instantToOffsetDateTime(instant: Instant): OffsetDateTime =
        instant.toOffsetDateTimeWithoutZone()

    @Named("getUserFullName")
    fun getUserFullName(user: User) = "${user.firstName} ${user.lastName}"
}

package eagle.dev.houseofculture.user.util

import eagle.dev.houseofculture.openapi.model.UserInfoTs
import eagle.dev.houseofculture.openapi.model.UserTs
import eagle.dev.houseofculture.user.model.User
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
@JvmDefaultWithCompatibility
interface UserMapper {

    fun userToUserTs(user: User): UserTs

    fun userToUserInfoTs(user: User): UserInfoTs
}
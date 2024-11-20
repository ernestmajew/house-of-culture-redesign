package eagle.dev.houseofculture.user.util

import eagle.dev.houseofculture.openapi.model.UserRoleTs
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
@JvmDefaultWithCompatibility
interface UserRoleMapper {

    fun userRoleToUserRoleTs(userRole: UserRole): UserRoleTs;
    fun userRoleTsToUserRole(userRole: UserRoleTs): UserRole;

}
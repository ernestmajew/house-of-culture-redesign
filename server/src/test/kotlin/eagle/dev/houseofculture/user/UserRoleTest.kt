package eagle.dev.houseofculture.user

import eagle.dev.houseofculture.user.model.enumeration.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserRoleTest {

    @Test
    fun isRoleAllowed() {
        // Given
        val role = UserRole.ADMIN
        val allowedRole = UserRole.EMPLOYEE

        // When
        val isRoleAllowed = role.isRoleAllowed(allowedRole)

        // Then
        assert(isRoleAllowed)
    }

    @Test
    fun getAllAllowedRoles() {
        // Given
        val role = UserRole.ADMIN

        // When
        val rolesAllowed = role.getAllRoles()

        // Then
        assertEquals(rolesAllowed, listOf(UserRole.EMPLOYEE, UserRole.INSTRUCTOR, UserRole.CLIENT, UserRole.CHILD, UserRole.ADMIN))
    }
}
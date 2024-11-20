package eagle.dev.houseofculture.user.repository

import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import org.springframework.data.repository.ListCrudRepository

interface UserRepository : ListCrudRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findAllByRoleIn(roles: List<UserRole>) : List<User>
}
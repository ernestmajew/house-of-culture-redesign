package eagle.dev.houseofculture.user.validator

import eagle.dev.houseofculture.exceptions.BadRequestException
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.ForbiddenException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.repository.UserRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserValidator(
    private var userRepository: UserRepository,
){
    fun validateCreateUser(user: User){
        with(user){
            validateEmailInUse(email)
            validateBirthday(birthdate, role)
        }
    }

    fun validateUpdateUser(user: User){
        with(user){
            validateBirthday(birthdate, role)
        }
    }

    fun isRoleAllowed(userRole: UserRole, desiredRole: UserRole){
        if(!userRole.getAllRoles().contains(desiredRole))
            throw UnprocessableEntityException("Role ${userRole.name} is not allowed")
    }

    fun validateIsUserChild(user: User, child: User) {
        require(user is Client) { throw ForbiddenException("User is a Child") }

        require(user.children.contains(child)) { throw ForbiddenException("User is not your Child") }
    }

    private fun validateBirthday(birthdate: LocalDate?, role: UserRole) {
        if(birthdate != null && birthdate.isAfter(LocalDate.now()))
            throw BadRequestException("Birthdate cannot be in the future")

        if(birthdate != null && role != UserRole.CHILD && birthdate.isAfter(LocalDate.now().minusYears(18)))
            throw BadRequestException("User must be at least 18 years old")
    }

    private fun validateEmailInUse(email: String){
        if(userRepository.existsByEmail(email))
            throw ConflictException("Email in use")
    }
}
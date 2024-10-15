package eagle.dev.houseofculture.user.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnauthorizedException
import eagle.dev.houseofculture.mail.MailSender
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.user.model.Child
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import eagle.dev.houseofculture.user.repository.UserRepository
import eagle.dev.houseofculture.user.util.UserMapper
import eagle.dev.houseofculture.user.util.UserRoleMapper
import eagle.dev.houseofculture.user.validator.UserValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class UserService(
    private val authService: AuthService,
    private val userMapper: UserMapper,
    private val userValidator: UserValidator,
    private val userRoleMapper: UserRoleMapper,
    private val userRepository: UserRepository,
    private val mailSender: MailSender,
    @Value("\${app.url.frontend}") private val defaultFrontendUrl: String,
    @Value("\${app.institution.name}") private val institutionName: String
) {

    fun getLoggedUserInfo(): UserTs {
        val user = authService.loggedInUserOrException()
        return userMapper.userToUserTs(user)
    }

    fun updateLoggedUser(updateUserRequestTs: UpdateUserRequestTs): UserTs {
        val user = authService.loggedInUserOrException()
        with(updateUserRequestTs){
            user.firstName = firstName
            user.lastName = lastName
            user.birthdate = birthdate
            user.getsNewsletter = getsNewsletter
            if(phoneNumber != null) user.phoneNumber = phoneNumber
        }
        this.userValidator.validateUpdateUser(user)
        val updatedUser = userRepository.save(user)

        return userMapper.userToUserTs(updatedUser)
    }

    fun createNewUser(request: CreateUserRequestTs) : UserTs {
        val role = getUserRole(request.role)

        val user: User = if (role == UserRole.CHILD ) {
            val loggedUser = authService.loggedInUserOrException()
            this.createChild(request, loggedUser)
        }else{
            this.createClient(request, role)
        }

        userValidator.validateCreateUser(user)
        val createdUser = userRepository.save(user)

        this.sendActivationMail(createdUser)

        return userMapper.userToUserTs(createdUser)
    }

    fun getChildren(): List<UserInfoTs> {
        val user = authService.loggedInUserOrException()
        if (user.role == UserRole.CHILD) return emptyList()

        return (user as Client).children.map { it -> userMapper.userToUserInfoTs(it) }
    }

    fun getUserById(id: Long): User =
        userRepository.findById(id).orElseThrow {
            throw ObjectNotFoundException("User with id $id doesn't exist.")
        }

    fun getUsersByIds(ids: List<Long>): List<User> =
        userRepository.findAllById(ids).also {
            if(ids.size != it.size) throw ObjectNotFoundException("User doesn't exist.")
        }

    fun getInstructors(): List<UserInfoTs> {
        val rolesToQuery = listOf(UserRole.INSTRUCTOR, UserRole.ADMIN)
        return userRepository.findAllByRoleIn(rolesToQuery)
            .map { it -> userMapper.userToUserInfoTs(it) }
    }

    private fun getUserRole(role: UserRoleTs): UserRole {
        val newRole = userRoleMapper.userRoleTsToUserRole(role);
        val user = this.authService.loggedInUserOrException()

        if (user.role == UserRole.ADMIN) return newRole;

        //only child cannot add child
        if (newRole == UserRole.CHILD && user.role != UserRole.CHILD) {
            return newRole
        }else{
            throw UnauthorizedException("Not authorized action")
        }
    }

    private fun sendActivationMail(user: User) {
        val passwordChangeRequest = this.authService.passwordChangeRequest(user.email)
        this.mailSender.sendMailWithTemplate(
            subject = "Aktywacja konta w $institutionName",
            targetEmail = user.email,
            templateKey = "mail.account.activation",
            passwordChangeRequest.code,
            "${defaultFrontendUrl}/password-change/${passwordChangeRequest.id}"
        )
    }

    private fun createChild(request: CreateUserRequestTs, user: User): User {
        val child = Child(
            firstName = request.name,
            lastName = request.lastname,
            email = request.email,
            birthdate = request.dateOfBirth,
            password = "DEFAULT_VALUE",
            status = UserStatus.INACTIVE,
            phoneNumber = request.phoneNumber,
            parent = user as Client
        )
        return child;
    }

    private fun createClient(request: CreateUserRequestTs, role: UserRole): User {
        val client = Client(
            firstName = request.name,
            lastName = request.lastname,
            email = request.email,
            birthdate = request.dateOfBirth,
            password = "DEFAULT_VALUE",
            status = UserStatus.INACTIVE,
            phoneNumber = request.phoneNumber,
            role = role,
        )
        return client;
    }


}
package eagle.dev.houseofculture.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import eagle.dev.houseofculture.auth.model.PasswordChange
import eagle.dev.houseofculture.auth.model.PasswordChangeStatus
import eagle.dev.houseofculture.auth.repository.PasswordChangeRepository
import eagle.dev.houseofculture.auth.util.VerificationCode
import eagle.dev.houseofculture.exceptions.*
import eagle.dev.houseofculture.mail.MailSender
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.security.MyUserDetails
import eagle.dev.houseofculture.security.jwt.JwtService
import eagle.dev.houseofculture.user.model.Child
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import eagle.dev.houseofculture.user.repository.UserRepository
import eagle.dev.houseofculture.user.validator.PasswordValidator
import eagle.dev.houseofculture.user.validator.UserValidator
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val userValidator: UserValidator,
    private val passwordValidator: PasswordValidator,
    private val verificationCode: VerificationCode,
    private val passwordChangeRepository: PasswordChangeRepository,
    private val mailSender: MailSender,
    @Value("\${app.url.frontend}") private val defaultFrontendUrl: String,
    @Value("\${app.institution.name}") private val institutionName: String
) {

    private val NO_USER: String = "No such user found";

    fun register(request: RegisterRequestTs): AuthResponseTs {
        val user = Client(
            firstName = request.name,
            lastName = request.lastname,
            email = request.email,
            birthdate = request.dateOfBirth,
            password = passwordEncoder.encode(request.password),
            status = UserStatus.ACTIVE,
            phoneNumber = request.phoneNumber,
            role = UserRole.CLIENT,
        )
        userValidator.validateCreateUser(user)
        passwordValidator.validate(request.password)

        userRepository.save(user)
        val jwtToken = jwtService.generateToken(MyUserDetails(user))
        val refreshToken = jwtService.generateRefreshToken(MyUserDetails(user))

        return AuthResponseTs(token = jwtToken, refreshToken = refreshToken)
    }

    fun authenticate(request: AuthRequestTs): AuthResponseTs {
        val user: User = userRepository.findByEmail(request.login) ?: throw UnauthorizedException(NO_USER)

        with(request) {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(login, password)
            )
        }

        val jwtToken = jwtService.generateToken(MyUserDetails(user))
        val refreshToken = jwtService.generateRefreshToken(MyUserDetails(user))

        return AuthResponseTs(token = jwtToken, refreshToken = refreshToken)
    }

    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }
        val refreshToken: String = authHeader.substring(7)
        jwtService.extractUsername(refreshToken)?.let {
            val userDetails = userDetailsService.loadUserByUsername(it)
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                val token = jwtService.generateToken(userDetails)
                val authResponse = AuthResponseTs(token = token, refreshToken = refreshToken)
                ObjectMapper().writeValue(response.outputStream, authResponse)
            }
        }
    }

    fun passwordChangeRequestTs(email: String): PasswordChangeInfoTs {
        val result = this.passwordChangeRequest(email)

        this.sendPasswordChangeMail(result, email)

        return PasswordChangeInfoTs(
            uuid = result.id!!,
            email = email,
            expirationDate = result.expirationTime.atOffset(java.time.ZoneOffset.UTC)
        )
    }

    fun getPasswordChangeRequest(uuid: UUID): PasswordChange {
        return passwordChangeRepository.findById(uuid)
            .orElseThrow { NotFoundException("No such request") }
    }

    fun getPasswordChangeRequestTs(uuid: UUID): PasswordChangeInfoTs {
        val passwordChange = getPasswordChangeRequest(uuid)
        return PasswordChangeInfoTs(
            uuid = passwordChange.id!!,
            email = passwordChange.account?.email!!,
            expirationDate = passwordChange.expirationTime.atOffset(java.time.ZoneOffset.UTC)
        )
    }

    fun passwordChangeRequest(email: String): PasswordChange {
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException("No such user found")
        val code = verificationCode.generateCode()
        val passwordChange = PasswordChange(
            account = user,
            code = code
        )
        return passwordChangeRepository.save(passwordChange)
    }


    fun validateChangePasswordCode(uuid: UUID, code: String, checkExpiration: Boolean = true) {
        if (!verificationCode.validateCode(code)) {
            throw UnauthorizedException("Invalid code")
        }
        val passwordChangeRequest = passwordChangeRepository.findById(uuid)
            .orElseThrow { NotFoundException("Invalid code") }

        if (passwordChangeRequest.code != code || passwordChangeRequest.status != PasswordChangeStatus.PENDING) {
            throw UnauthorizedException("Invalid code")
        }
        if (checkExpiration && passwordChangeRequest.expirationTime.isBefore(java.time.Instant.now())) {
            passwordChangeRequest.status = PasswordChangeStatus.EXPIRED
            throw ConflictException("Code expired")
        }
    }

    fun changeUserPassword(passwordChangeRequestTs: PasswordChangeRequestTs) {
        with(passwordChangeRequestTs) {
            validateChangePasswordCode(uuid, code, checkExpiration = false)
            val passwordChangeRequest = passwordChangeRepository.findById(uuid)
                .orElseThrow { UnauthorizedException("Invalid code") }

            val user = passwordChangeRequest.account
                ?: throw UnauthorizedException("Invalid code")

            passwordValidator.validate(password)
            user.password = passwordEncoder.encode(password)
            user.status = UserStatus.ACTIVE
            passwordChangeRequest.status = PasswordChangeStatus.USED
            userRepository.save(user)
        }
    }

    fun getLoggedInUser(): User? {
        val email = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByEmail(email)
    }

    fun loggedInUserOrException(): User =
        getLoggedInUser() ?: throw UnauthorizedException("User is not logged in")

    // validate action when logged-in user is Client and want to perform action as him or his child
    fun getLoggedInClientOrHisChild(userId: Long?): User {
        val loggedInUser = loggedInUserOrException().also {
            if(it is Child) throw ForbiddenException("Child cannot perform given action.")
        } as Client

        if (userId != null
            && loggedInUser.id != userId
            && !loggedInUser.children.map { it.id }.contains(userId)
        ) {
            throw ForbiddenException("User cannot get not his child in this action.")
        }

        // user existence checked in previous condition
        return userRepository.findById(userId!!).get()
    }


    private fun sendPasswordChangeMail(passwordChange: PasswordChange, mail: String) {
        this.mailSender.sendMailWithTemplate(
            subject = "Zmiana hasła w $institutionName",
            targetEmail = mail,
            templateKey = "mail.password-change",
            passwordChange.code,
            "${defaultFrontendUrl}/password-change/${passwordChange.id}"
        )
    }
}
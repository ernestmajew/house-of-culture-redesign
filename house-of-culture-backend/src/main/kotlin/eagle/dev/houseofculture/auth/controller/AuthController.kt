package eagle.dev.houseofculture.auth.controller

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.openapi.api.AuthApi
import eagle.dev.houseofculture.openapi.model.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@Transactional
class AuthController(
    private val authService: AuthService
): AuthApi{

    override fun authenticate(authRequestTs: AuthRequestTs): ResponseEntity<AuthResponseTs> =
        ResponseEntity.ok(authService.authenticate(authRequestTs))

    override fun register(registerRequestTs: RegisterRequestTs): ResponseEntity<AuthResponseTs> =
        ResponseEntity.ok(authService.register(registerRequestTs))

    override fun changePasswordRequest(changePasswordRequestRequestTs: ChangePasswordRequestRequestTs): ResponseEntity<PasswordChangeInfoTs> =
        ResponseEntity.ok(authService.passwordChangeRequestTs(changePasswordRequestRequestTs.email))

    override fun validateChangePasswordCode(uuid: UUID, code: String): ResponseEntity<Unit> =
        ResponseEntity.ok(authService.validateChangePasswordCode(uuid, code))

    override fun changeUserPassword(passwordChangeRequestTs: PasswordChangeRequestTs): ResponseEntity<Unit> =
        ResponseEntity.ok(authService.changeUserPassword(passwordChangeRequestTs))

    override fun getPasswordChangeInfo(uuid: UUID): ResponseEntity<PasswordChangeInfoTs> =
        ResponseEntity.ok(authService.getPasswordChangeRequestTs(uuid))

    @PostMapping("api/auth/refresh-token")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse) =
        authService.refreshToken(request, response)
}
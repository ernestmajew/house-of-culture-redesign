package eagle.dev.houseofculture.user.controller

import eagle.dev.houseofculture.openapi.api.UserApi
import eagle.dev.houseofculture.openapi.model.CreateUserRequestTs
import eagle.dev.houseofculture.openapi.model.UpdateUserRequestTs
import eagle.dev.houseofculture.openapi.model.UserInfoTs
import eagle.dev.houseofculture.openapi.model.UserTs
import eagle.dev.houseofculture.user.service.UserService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional
class UserController(
    private val userService: UserService
): UserApi {
    override fun getAccount(): ResponseEntity<UserTs> =
        ResponseEntity.ok(userService.getLoggedUserInfo())

    override fun updateAccount(updateUserRequestTs: UpdateUserRequestTs) =
        ResponseEntity.ok(userService.updateLoggedUser(updateUserRequestTs))

    override fun createUser(createUserRequestTs: CreateUserRequestTs): ResponseEntity<UserTs> =
        ResponseEntity.ok(userService.createNewUser(createUserRequestTs))

    override fun getChildren(): ResponseEntity<List<UserInfoTs>> =
        ResponseEntity.ok(userService.getChildren())

    override fun getAllInstructors(): ResponseEntity<List<UserInfoTs>> =
        ResponseEntity.ok(userService.getInstructors())
}
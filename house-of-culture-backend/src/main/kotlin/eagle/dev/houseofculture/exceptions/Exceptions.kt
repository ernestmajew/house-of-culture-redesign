package eagle.dev.houseofculture.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

sealed class CustomException(status: HttpStatus, message: String): ResponseStatusException(status, message)

class BadRequestException(message: String): CustomException(HttpStatus.BAD_REQUEST, message)
class ObjectNotFoundException(message: String): CustomException(HttpStatus.NOT_FOUND, message)
class UnauthorizedException(message: String): CustomException(HttpStatus.UNAUTHORIZED, message)
class ForbiddenException(message: String): CustomException(HttpStatus.FORBIDDEN, message)
class ConflictException(message: String): CustomException(HttpStatus.CONFLICT, message)
class NotFoundException(message: String): CustomException(HttpStatus.NOT_FOUND, message)
class UnprocessableEntityException(message: String): CustomException(HttpStatus.UNPROCESSABLE_ENTITY, message)
class InternalServerError(message: String): CustomException(HttpStatus.INTERNAL_SERVER_ERROR, message)
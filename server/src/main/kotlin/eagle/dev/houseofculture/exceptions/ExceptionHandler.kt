package eagle.dev.houseofculture.exceptions

import eagle.dev.houseofculture.openapi.model.ErrorTs
import org.springframework.core.NestedExceptionUtils.getRootCause
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

data class ErrorResponse(val message: String)

fun Throwable.toResponse(): ErrorResponse = ErrorResponse(message ?: "No message available")

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val error = getRootCause(ex)?.toResponse() ?: ErrorResponse(ex.message ?: "No message available")
        logger.error("Invalid request to ${(request as? ServletWebRequest)?.request?.requestURI} ${error.message} (Status:${status.value()})")

        return ErrorTs(error.message, java.time.OffsetDateTime.now()).let {
            ResponseEntity.status(status).body(it)
        }
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val error = getRootCause(ex)?.toResponse() ?: ErrorResponse(ex.message)
        logger.error("Invalid request body to ${(request as? ServletWebRequest)?.request?.requestURI} ${error.message}")

        return ErrorTs(message=error.message, timestamp = java.time.OffsetDateTime.now()).let {
            ResponseEntity.status(status).body(it)
        }
    }

    @ExceptionHandler
    fun handleCustomException(ex: CustomException, request: WebRequest): ResponseEntity<ErrorTs> {
        logger.error("Custom exception ${(request as? ServletWebRequest)?.request?.requestURI} ${ex.message}")

        return ErrorTs(message=ex.message, timestamp = java.time.OffsetDateTime.now()).let {
            ResponseEntity.status(ex.statusCode).body(it)
        }
    }
}
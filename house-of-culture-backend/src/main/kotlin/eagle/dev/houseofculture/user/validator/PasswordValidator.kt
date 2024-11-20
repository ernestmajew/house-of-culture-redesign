package eagle.dev.houseofculture.user.validator

import eagle.dev.houseofculture.exceptions.BadRequestException
import org.springframework.stereotype.Component

@Component
class PasswordValidator {

    fun validate(password: String) {
        if(!validatePassword(password))
            throw BadRequestException("Password must be at least 8 characters long, contain at least one digit, one lowercase letter, one uppercase letter and one special character")
    }
    private fun validatePassword(password: String) = password.length >= 8 &&
                password.contains(Regex("^(?=.*[0-9]).*$")) &&
                password.contains(Regex("^(?=.*[a-z]).*$")) &&
                password.contains(Regex("^(?=.*[A-Z]).*$")) &&
                password.contains(Regex("^(?=.*[!@#$%^&+=]).*$"))
}
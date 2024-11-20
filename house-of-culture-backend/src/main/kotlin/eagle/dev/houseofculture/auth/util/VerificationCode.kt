package eagle.dev.houseofculture.auth.util

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class VerificationCode {

    //ISO 7064 Mod 97,10
    fun generateCode(): String {
        val randomNumber = Random.nextInt(10000) // Generate a random number between 0 and 9999
        val formattedRandomNumber = "%04d".format(randomNumber) // Format as a 4-character string with leading zeros
        val formattedCheckSum = "%02d".format(getCheckSum(randomNumber))

        return "$formattedRandomNumber$formattedCheckSum"
    }

    fun validateCode(code: String): Boolean {
        return code.toInt() % 97 == 1
    }

    private fun getCheckSum(code: Int): Int{
         return (98 - (code * 100 % 97)) % 97
    }
}
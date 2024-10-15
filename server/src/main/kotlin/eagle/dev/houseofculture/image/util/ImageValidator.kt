package eagle.dev.houseofculture.image.util

import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.buffer.PeekableInputStream
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.net.URLConnection
import java.util.*

@Component
class ImageValidator {

    private val fileSignatureToExtension = setOf("image/jpeg", "image/jpg", "image/png", "image/gif")

    fun validateImageFile(file: ByteArray) {
        if (!isValidImageFile(file)) {
            throw UnprocessableEntityException(
                "Invalid file extension. Accepted extensions: ${fileSignatureToExtension.joinToString()}"
            )
        }
    }

    private fun isValidImageFile(file: ByteArray): Boolean {
        val mimeType = detectMimeType(file)
        return fileSignatureToExtension.contains(mimeType.toLowerCase(Locale.ROOT))
    }

    private fun detectMimeType(file: ByteArray): String {
        val inputStream = PeekableInputStream(ByteArrayInputStream(file))
        val fileSignature = IOUtils.toByteArray(inputStream, 8)
        val mimeType = URLConnection.guessContentTypeFromStream(ByteArrayInputStream(fileSignature))

        return if (mimeType.isNullOrBlank()) {
            FilenameUtils.getExtension(FilenameUtils.getName("file.${fileSignature.toHexString()}"))
        } else {
            mimeType
        }
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}
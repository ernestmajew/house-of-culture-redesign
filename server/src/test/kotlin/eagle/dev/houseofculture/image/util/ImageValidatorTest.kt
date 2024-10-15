package eagle.dev.houseofculture.image.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.InputStream
import java.net.URLConnection

class ImageValidatorTest {

    private val imageValidator = ImageValidator()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockStatic(URLConnection::class.java)
    }

    @Test
    fun `getFileExtensionFromByteArray should return correct file extension for valid file`() {
        // Given
        val validImageFile = byteArrayOf(0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00)

        // When
        `when`(URLConnection.guessContentTypeFromStream(any(InputStream::class.java))).thenReturn("image/gif")

        // Then
        assertDoesNotThrow { imageValidator.validateImageFile(validImageFile) }
    }

}
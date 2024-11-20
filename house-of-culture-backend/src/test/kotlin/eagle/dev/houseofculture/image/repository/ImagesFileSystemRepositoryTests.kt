package eagle.dev.houseofculture.image.repository

import eagle.dev.houseofculture.image.util.ImageValidator
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class ImagesFileSystemRepositoryTests {

    @Value("\${app.images.location}")
    private lateinit var imagesPath: String

    private lateinit var imagesFileSystemRepository: ImagesFileSystemRepository

    private val imageValidator: ImageValidator = mockk()

    @BeforeEach
    fun setUp() {
        imagesFileSystemRepository = ImagesFileSystemRepository(imagesPath, imageValidator)
    }

    @Test
    fun getFile() {
        val filename = "fileToGet.jpg"
        val content = ByteArray(10)

        // Given
        saveFile(content, Paths.get("${imagesPath}/${filename}"))

        // When
        val fileFromFileSystem = imagesFileSystemRepository.get(filename)

        // Then
        assert(fileFromFileSystem.contentAsByteArray.contentEquals(content))
    }

    @Test
    fun saveFile() {
        val filename = "fileToSave.jpg"

        // Given
        assert(!Files.exists(Paths.get(filename)))
        every { imageValidator.validateImageFile(any()) } just runs

        // When
        val savedPath = imagesFileSystemRepository.save(ByteArray(10), "", filename)

        // Then
        assert(Files.exists(Paths.get("${imagesPath}/${savedPath}")))
    }

    @Test
    fun deleteFile() {
        val filename = "file"

        // Given
        saveFile(ByteArray(10), Paths.get("${imagesPath}/${filename}"))

        // When
        imagesFileSystemRepository.delete(filename)

        // Then
        assert(!Files.exists(Paths.get("${imagesPath}/${filename}")))
    }

    @Test
    @DisplayName("Throws exception for not existing file")
    fun `throw exception for not existing file`() {
        assertThrows<IOException> { imagesFileSystemRepository.get("NOT_EXISTING_FILE") }
    }

    @AfterEach
    fun clearImagesDirectory() {
        val imagesDirectory = File(imagesPath)
        for (file in imagesDirectory.listFiles() ?: emptyArray()) {
            Files.delete(file.toPath())
        }
        imagesDirectory.delete()
    }

    private fun saveFile(content: ByteArray, path: Path) {
        Files.createDirectories(path.parent)
        Files.write(path, content)
    }
}
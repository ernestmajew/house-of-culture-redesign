package eagle.dev.houseofculture.image.service

import eagle.dev.houseofculture.image.repository.ImagesRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.io.Resource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Base64.getEncoder

class ImageServiceTest {
    private lateinit var imageService: ImageService
    private val imagesRepository: ImagesRepository = mockk()

    private val BASE_64_FILE: String
    private val PATH: String = "/test/image"
    private val FILENAME: String = "logo.png"
    private val EXPECTED_PATH: String = "images/test/image/image.jpg"
    private val EXPECTED_CONTENT: ByteArray

    init {
        val imagePath: Path = Paths.get("images/test/logo.png")
        EXPECTED_CONTENT = Files.readAllBytes(imagePath)
        BASE_64_FILE = getEncoder().encodeToString(EXPECTED_CONTENT)
    }

    @BeforeEach
    fun setUp() {
        imageService = ImageService(imagesRepository)
    }

    @Nested
    inner class SaveImage {
        @Test
        fun `saveImage should call imagesRepository save method`() {
            // Given
            every { imagesRepository.save(any(), any(), any()) } returns EXPECTED_PATH

            // When
            val result = imageService.saveImage(BASE_64_FILE, PATH, FILENAME)

            // Then
            assertEquals(EXPECTED_PATH, result)
            verify { imagesRepository.save(EXPECTED_CONTENT, PATH, FILENAME) }
        }
    }

    @Nested
    inner class ClearDirectory {
        @Test
        fun `clearDirectory should call imagesRepository clearDirectory method`() {
            // Given
            every { imagesRepository.clearDirectory(any()) } just runs

            // When
            imageService.clearDirectory(PATH)

            // Then
            verify { imagesRepository.clearDirectory(PATH) }
        }

    }

    @Nested
    inner class GetFirstImage {
        @Test
        fun `getFirstImage should call imagesRepository getFirstImageInDirectory method`() {
            // Given
            every { imagesRepository.getFirstImageInDirectory(PATH) } returns FILENAME

            // When
            val result = imageService.getFirstImage(PATH)

            // Then
            assertEquals(FILENAME, result)
            verify { imagesRepository.getFirstImageInDirectory(PATH) }
        }

    }

    @Nested
    inner class GetAllImages {
        @Test
        fun `getAllImages should call imagesRepository getAllImagesInDirectory method`() {
            // Given
            val expectedImages = listOf(FILENAME)

            every { imagesRepository.getAllImagesInDirectory(PATH) } returns expectedImages

            // When
            val result = imageService.getAllImages(PATH)

            // Then
            assertEquals(expectedImages, result)
            verify { imagesRepository.getAllImagesInDirectory(PATH) }
        }
    }

    @Nested
    inner class GetImage {
        @Test
        fun `getImage should call imagesRepository get method`() {
            // Given
            val expectedResource: Resource = mockk()

            every { imagesRepository.get(PATH) } returns expectedResource

            // When
            val result = imageService.getImage(PATH)

            // Then
            assertEquals(expectedResource, result)
            verify { imagesRepository.get(PATH) }
        }
    }
}
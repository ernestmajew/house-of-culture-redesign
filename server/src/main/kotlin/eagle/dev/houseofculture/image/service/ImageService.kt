package eagle.dev.houseofculture.image.service

import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.image.repository.ImagesRepository
import org.springframework.stereotype.Service
import java.io.IOException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class ImageService(
    val imagesRepository: ImagesRepository
) {

    @OptIn(ExperimentalEncodingApi::class)
    fun saveImage(base64Image: String, path: String, filename: String): String {
        val imageData = Base64.decode(base64Image);
        return imagesRepository.save(imageData, path, filename)
    }

    fun clearDirectory(path: String) =
        imagesRepository.clearDirectory(path)

    fun getFirstImage(path: String) =
        try {
            imagesRepository.getFirstImageInDirectory(path)
        } catch (e: IOException) {
            throw ObjectNotFoundException(e.message!!)
        }

    fun getAllImages(path: String) =
        try {
            imagesRepository.getAllImagesInDirectory(path)
        } catch (e: IOException) {
            throw ObjectNotFoundException(e.message!!)
        }

    fun getImage(path: String) =
        try {
            imagesRepository.get(path)
        } catch (e: IOException) {
            throw ObjectNotFoundException(e.message!!)
        }
}
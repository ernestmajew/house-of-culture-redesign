package eagle.dev.houseofculture.image.repository

import org.springframework.core.io.Resource

interface ImagesRepository {
    fun save(content: ByteArray, path: String, fileName: String): String
    fun delete(path: String)
    fun getAllImagesInDirectory(directoryPath: String): List<String>
    fun getFirstImageInDirectory(directoryPath: String): String
    fun get(path: String): Resource
    fun clearDirectory(directoryPath: String)
}
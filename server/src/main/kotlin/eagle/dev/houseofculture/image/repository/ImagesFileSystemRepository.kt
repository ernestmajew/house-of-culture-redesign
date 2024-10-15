package eagle.dev.houseofculture.image.repository

import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.image.util.ImageValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


@Repository
class ImagesFileSystemRepository(
    @Value("\${app.images.location}") val imagesDirectory: String,
    val imageValidator: ImageValidator
) : ImagesRepository {

    override fun save(content: ByteArray, path: String, fileName: String): String {
        imageValidator.validateImageFile(content);

        val timestamp = System.currentTimeMillis()
        val fileToSave = Paths.get("${path.toFullPath()}/$timestamp-$fileName")

        Files.createDirectories(fileToSave.parent)
        Files.write(fileToSave, content)
        return fileToSave.toString().substringAfter(imagesDirectory)
    }

    @Throws(IOException::class)
    override fun delete(path: String) {
        val fullPath = path.toFullPath()
        if (Files.exists(fullPath)) {
            try {
                Files.delete(fullPath)
            } catch (e: IOException) {
                throw ObjectNotFoundException(e.message!!)
            }
        }
    }

    override fun getAllImagesInDirectory(directoryPath: String): List<String> {
        val directory = directoryPath.toFullPath().toFile()

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        var files = directory.listFiles()

        if (files != null) {
            files = files.sortedBy { it.name }.toTypedArray()
        }

        if (files.isNullOrEmpty()) {
            return emptyList()
        }

        return files.map { it.toString().substringAfter("images") }
    }


    override fun getFirstImageInDirectory(directoryPath: String): String {
        val allImages = getAllImagesInDirectory(directoryPath)
        if (allImages.isEmpty()) {
            return ""
        }
        return allImages.first()
    }

    override fun get(path: String): Resource {
        val file = path.toFullPath().toFile().apply {
            if (!this.exists()) throw IOException("File $path doesn't exist")
        }

        return FileSystemResource(file)
    }

    override fun clearDirectory(directoryPath: String) {
        getAllImagesInDirectory(directoryPath).forEach(this::delete) // delete files from directory
        Files.deleteIfExists(directoryPath.toFullPath()) // delete empty directory
    }

    private fun String.toFullPath() =
        Paths.get("${imagesDirectory}/${this}")
}
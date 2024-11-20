package eagle.dev.houseofculture.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.random.Random

@Component
class DatabasePopulation(
    @Value("\${app.images.location}") val imagesDir: String
): ApplicationListener<ContextRefreshedEvent?> {
    val sampleImagesDir = "sample-images"

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if(!Files.exists(Path.of("$imagesDir/posts"))) {
            val postsIds = IntArray(14) { i -> i + 1 }
            populateImages(postsIds, "$imagesDir/posts")
        }

        if(!Files.exists(Path.of("$imagesDir/events"))) {
            val eventsIds = IntArray(10) { i -> i + 1 }
            populateImages(eventsIds, "$imagesDir/events")
        }
    }

    private fun populateImages(ids: IntArray, dir: String) =
        ids.forEach { id ->
            Files.createDirectories(Path.of("$dir/$id"))
            getRandomImages().forEachIndexed { index, image ->
                copyFile(image, "$dir/$id/$index.jpg")
            }
        }

    private fun getRandomImages() =
        IntArray(5) {i -> i + 1}
            .also { it.shuffle() }
            .take(Random.nextInt(1,6)) // select random 1-5 number
            .map { "$sampleImagesDir/$it.jpg" }

    private fun copyFile(from: String, to: String) =
        Files.copy(Path.of(from), Path.of(to), StandardCopyOption.REPLACE_EXISTING)
}
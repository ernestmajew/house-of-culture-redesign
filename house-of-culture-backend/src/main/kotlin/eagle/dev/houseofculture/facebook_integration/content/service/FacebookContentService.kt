package eagle.dev.houseofculture.facebook_integration.content.service

import com.restfb.*
import com.restfb.batch.BatchRequest.BatchRequestBuilder
import com.restfb.json.JsonArray
import com.restfb.json.JsonObject
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.InternalServerError
import eagle.dev.houseofculture.facebook_integration.authentication.model.FacebookApiData
import eagle.dev.houseofculture.facebook_integration.authentication.service.FacebookAuthenticationService
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class FacebookContentService(
    private val authenticationService: FacebookAuthenticationService,
) {
    // Returns ID of new created Facebook Post on feed
    fun createPost(description: String, images: List<Resource>): String {
        val apiData = authenticationService.getApiData()
        val client = buildFacebookClient(apiData)

        // upload and transform images to attachment required format
        val attachedImages = uploadImages(images, client, apiData.pageId!!)
            .toList()
            .fold(JsonArray()) { acc, imageId ->
                acc.add(JsonObject().add("media_fbid", imageId))
            }

        val response = client.publish(
            "${apiData.pageId}/feed",
            JsonObject::class.java,
            Parameter.with("message", description),
            Parameter.with(
                "attached_media",
                attachedImages
            )
        )

        return response.getString("id", "").also {
            if(it.isEmpty()) throw InternalServerError("Cannot create facebook post. Response: $response")
        }
    }

    fun deletePost(id: String) {
        val apiData = authenticationService.getApiData()
        val client = buildFacebookClient(apiData)

        if(!client.deleteObject(id)) throw InternalServerError("Cannot delete facebook post with id: $id.")
    }

    // return List<imageId> with unchanged order
    private fun uploadImages(images: List<Resource>, client: DefaultFacebookClient, pageId: String): List<String> {
        val batchRequests = List(images.size) { index ->
            BatchRequestBuilder("${pageId}/photos")
                .method("POST")
                .attachedFiles("image_$index") // need to use file without .ext
                .parameters(Parameter.with("published", false))
                .build()
        }

        val attachments = images.mapIndexed { index, image ->
            BinaryAttachment.with(
                "image_$index.png",
                image.contentAsByteArray
            )
        }

        val jsonMapper = DefaultJsonMapper()
        val responses = client
            .executeBatch(batchRequests, attachments)
            .map { jsonMapper.toJavaObject(it.body, JsonObject::class.java) }

        // validate
        responses.forEach {
            if(it.getString("id", "").isEmpty())
                throw InternalServerError("Cannot upload image to facebook. Response: $it")
        }

        return responses.map { it.getString("id", "") }
    }

    private fun buildFacebookClient(apiData: FacebookApiData): DefaultFacebookClient {
        val pageAccessToken = apiData.pageAccessToken
            ?: throw ConflictException("Facebook page is not integrated with app.")

        return DefaultFacebookClient(pageAccessToken, Version.LATEST)
    }
}
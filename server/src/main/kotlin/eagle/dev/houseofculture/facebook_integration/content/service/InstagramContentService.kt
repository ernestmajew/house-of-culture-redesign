package eagle.dev.houseofculture.facebook_integration.content.service

import com.restfb.DefaultFacebookClient
import com.restfb.DefaultJsonMapper
import com.restfb.Parameter
import com.restfb.Version
import com.restfb.batch.BatchRequest
import com.restfb.json.JsonObject
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.InternalServerError
import eagle.dev.houseofculture.facebook_integration.authentication.model.FacebookApiData
import eagle.dev.houseofculture.facebook_integration.authentication.service.FacebookAuthenticationService
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@Service
class InstagramContentService(
    private val authenticationService: FacebookAuthenticationService,
    private val imgBbUploadService: ImgBbUploadService
) {
    fun createPost(description: String, images: List<Resource>): String {
        val imagesFromPublicServer = runBlocking {
            val deferredResults = images.map { image ->
                async {
                    imgBbUploadService.uploadImage(image)
                }
            }

            deferredResults.map { it.await() }
        }


        val apiData = authenticationService.getApiData()
        val client = buildFacebookClient(apiData)

        val isCarousel = imagesFromPublicServer.size != 1 // carousel contains >1 images
        val imagesFromInstagram = uploadImagesToInstagram(imagesFromPublicServer, client, apiData.instagramProfileId!!, isCarousel)

        val postToPublishId = if(isCarousel) createCarouselPost(imagesFromInstagram, description, client, apiData.instagramProfileId!!) else imagesFromInstagram.first()

        val publishedPostId = publishCarouselPost(postToPublishId, client, apiData.instagramProfileId!!)

        return getPostPermalink(publishedPostId, client)
    }

    private fun createCarouselPost(
        igUploadedImages: List<String>,
        description: String,
        client: DefaultFacebookClient,
        instagramProfileId: String
    ) = client.publish(
            "/${instagramProfileId}/media",
            JsonObject::class.java,
            Parameter.with("media_type", "CAROUSEL"),
            Parameter.with("children", igUploadedImages),
            Parameter.with("caption", description)
        ).also {
            validateResponse(it, "Cannot create carousel post on Instagram")
        }.getString("id", "")

    private fun publishCarouselPost(
        postIdToPublish: String,
        client: DefaultFacebookClient,
        instagramProfileId: String
    ) = client.publish(
            "/${instagramProfileId}/media_publish",
            JsonObject::class.java,
            Parameter.with("creation_id", postIdToPublish),
        ).also {
            validateResponse(it, "Cannot publish post on Instagram")
        }.getString("id", "")

    // TODO: add validation
    private fun getPostPermalink(
        postId: String,
        client: DefaultFacebookClient
    ) = client.fetchObject(
        "/$postId",
        JsonObject::class.java,
        Parameter.with("fields", "permalink"),
    ).getString("permalink", "")

    // return List<imageId> with unchanged order
    private fun uploadImagesToInstagram(
        images: List<String>,
        client: DefaultFacebookClient,
        instagramProfileId: String,
        isCarouselItem: Boolean
    ): List<String> {
        val batchRequests = images.map {
            BatchRequest.BatchRequestBuilder("/${instagramProfileId}/media")
                .method("POST")
                .parameters(
                    Parameter.with("is_carousel_item", isCarouselItem),
                    Parameter.with("image_url", it),
                ).build()
        }

        val jsonMapper = DefaultJsonMapper()
        val responses = client
            .executeBatch(batchRequests)
            .map { jsonMapper.toJavaObject(it.body, JsonObject::class.java) }

        // validate
        responses.forEach {
            validateResponse(it, "Cannot upload image to Instagram")
        }

        return responses.map { it.getString("id", "") }
    }

    private fun buildFacebookClient(apiData: FacebookApiData): DefaultFacebookClient {
        val pageAccessToken = apiData.pageAccessToken
            ?: throw ConflictException("Facebook page is not integrated with app.")

        return DefaultFacebookClient(pageAccessToken, Version.LATEST)
    }

    private fun validateResponse(response: JsonObject, errorMessage: String) {
        if(response.getString("id", "").isEmpty())
            throw InternalServerError("$errorMessage. Response: $response")
    }
}
package eagle.dev.houseofculture.facebook_integration.content.service

import com.google.gson.JsonParser
import eagle.dev.houseofculture.exceptions.InternalServerError
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class ImgBbUploadService(
    @Value("\${social-media.imgbb.app-key}")private val apiKey: String,
    private val restTemplate: RestTemplate
) {
    val apiUrl = "https://api.imgbb.com/1/upload"

    @OptIn(ExperimentalEncodingApi::class)
    fun uploadImage(image: Resource): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val params = UriComponentsBuilder.newInstance()
            .queryParam("expiration", 1800) // 30 minutes
            .queryParam("key", apiKey)
            .queryParam("name", "image.jpeg")
            .build()
            .toUriString()

        val requestBody = LinkedMultiValueMap<String, String>()
        requestBody.add("image", Base64.encode(image.contentAsByteArray))

        val fullUrl = "$apiUrl$params"

        val response = try {
            restTemplate.exchange(
                fullUrl,
                HttpMethod.POST,
                HttpEntity(requestBody, headers),
                String::class.java
            ).let { JsonParser.parseString(it.body) }
        } catch (e: Exception) {
            throw InternalServerError("Cannot upload image to ImgBB. Error: ${e.message}")
        }

        return response.asJsonObject.get("data").asJsonObject.get("url").asString
    }
}
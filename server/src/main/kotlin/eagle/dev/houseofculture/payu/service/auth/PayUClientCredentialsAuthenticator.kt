package eagle.dev.houseofculture.payu.service.auth

import com.fasterxml.jackson.databind.ObjectMapper
import eagle.dev.houseofculture.payu.config.PayUConfigurationProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class PayUClientCredentialsAuthenticator(
    private val payUConfiguration: PayUConfigurationProperties,
    private val restTemplate: RestTemplate
) {
    companion object {
        const val GRANT_TYPE = "client_credentials"
    }

    fun authenticate(): PayUAuthToken {
        val authRequest = buildAuthorizationUri()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED


        val jsonResponse = restTemplate.postForEntity(
            authRequest, HttpEntity(null, headers), String::class.java
        )

        return ObjectMapper().readValue(jsonResponse.body, PayUAuthToken::class.java)
    }

    private fun buildAuthorizationUri(): String {
        with(payUConfiguration) {
            return "$authorizationUri?grant_type=$GRANT_TYPE&client_id=$clientId&client_secret=$clientSecret"
        }
    }


}
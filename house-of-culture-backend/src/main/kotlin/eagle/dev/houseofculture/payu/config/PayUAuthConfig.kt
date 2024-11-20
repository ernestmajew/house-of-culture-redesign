package eagle.dev.houseofculture.payu.config

import eagle.dev.houseofculture.payu.service.auth.PayUClientCredentialsAuthenticator
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate


@Configuration
internal class PayUAuthConfig(
    private val payUClientCredentialsAuthenticator: PayUClientCredentialsAuthenticator
) {

    @Bean("payuApiRestTemplate")
    fun payuRestTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory(customHttpClient())
        val restTemplate = RestTemplate(factory)

        restTemplate.interceptors = listOf(ClientHttpRequestInterceptor { httpRequest, bytes, clientHttpRequestExecution ->
            val payUAuthToken = payUClientCredentialsAuthenticator.authenticate()
            val headers = httpRequest.headers
            headers.add("Authorization", "Bearer ${payUAuthToken.accessToken}")
            if (!headers.containsKey("Content-Type")) {
                headers.add("Content-Type", "application/json")
            }
            clientHttpRequestExecution.execute(httpRequest, bytes)
        })

        return restTemplate
    }

    private fun customHttpClient() = HttpClients.custom()
        .disableRedirectHandling()
        .build()

}
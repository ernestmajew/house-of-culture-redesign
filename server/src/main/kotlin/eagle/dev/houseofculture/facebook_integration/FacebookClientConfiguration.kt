package eagle.dev.houseofculture.facebook_integration

import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.Version
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "social-media.meta")
class MetaApiProperties(
    val appId: String,
    val appAccessToken: String,
)

@Configuration
class FacebookClientConfig(
    private val properties: MetaApiProperties
) {
    @Bean
    fun facebookClient(): FacebookClient =
        DefaultFacebookClient(buildAccessToken(), Version.LATEST)

    private fun buildAccessToken() =
        with(properties) { "$appId|$appAccessToken" }
}
package eagle.dev.houseofculture.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app.security.jwt")
class JwtProperties {
    var secretKey: String? = null
    var expiration: Long = 0
    var refreshExpiration: Long = 0
}
package eagle.dev.houseofculture.facebook_integration.authentication.model

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "facebook_api_data")
data class FacebookApiData(
    @Enumerated(EnumType.STRING)
    var integrationStatus: IntegrationStatus = IntegrationStatus.INACTIVE,
    var userAccessToken: String?,
    var username: String?,
    var expirationTime: Instant?, // expiration time of main userAccessToken
    var pageAccessToken: String? = null,
    var pageId: String? = null,
    var pageName: String? = null,
    var instagramProfileId: String? = null,
    var instagramUsername: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

enum class IntegrationStatus {
    INACTIVE,
    PENDING,
    ACTIVE,
    EXPIRED
}
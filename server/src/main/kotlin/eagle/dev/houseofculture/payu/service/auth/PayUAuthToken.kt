package eagle.dev.houseofculture.payu.service.auth

import com.fasterxml.jackson.annotation.JsonAlias


class PayUAuthToken {
    @JsonAlias("access_token")
    val accessToken: String? = null

    @JsonAlias("token_type")
    val tokenType: String? = null

    @JsonAlias("expires_in")
    val expiresIn = 0

    @JsonAlias("grant_type")
    val grantType: String? = null
    val error: String? = null

    @JsonAlias("error_description")
    val errorDescription: String? = null
}
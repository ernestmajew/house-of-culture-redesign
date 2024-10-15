package eagle.dev.houseofculture.facebook_integration.authentication.controller

import eagle.dev.houseofculture.facebook_integration.authentication.service.FacebookAuthenticationService
import eagle.dev.houseofculture.facebook_integration.authentication.util.FacebookMapper
import eagle.dev.houseofculture.openapi.api.FacebookAuthenticationApi
import eagle.dev.houseofculture.openapi.model.ConnectFacebookPageRequestTs
import eagle.dev.houseofculture.openapi.model.FacebookAuthenticationDataResponseTs
import eagle.dev.houseofculture.openapi.model.FacebookPageResponseTs
import eagle.dev.houseofculture.openapi.model.FacebookUserCodeResponseTs
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional
class FacebookAuthenticationController(
    private val facebookAuthenticationService: FacebookAuthenticationService,
    private val mapper: FacebookMapper
): FacebookAuthenticationApi {
    override fun getFacebookAuthenticationData(): ResponseEntity<FacebookAuthenticationDataResponseTs> =
        facebookAuthenticationService
            .getApiData()
            .let(mapper::apiDataToTs)
            .let { ResponseEntity.ok(it) }

    override fun authenticateFacebookApi(): ResponseEntity<FacebookUserCodeResponseTs> =
        facebookAuthenticationService
            .generateUserCode()
            .let(mapper::deviceCodeToTs)
            .let { ResponseEntity.ok(it) }

    override fun getAvailableFacebookPages(): ResponseEntity<List<FacebookPageResponseTs>> =
        facebookAuthenticationService
            .getAllUserPages()
            .map(mapper::pageToTs)
            .let { ResponseEntity.ok(it) }

    override fun connectFacebookPage(
        connectFacebookPageRequestTs: ConnectFacebookPageRequestTs
    ): ResponseEntity<FacebookAuthenticationDataResponseTs> =
        facebookAuthenticationService
            .connectFacebookPage(connectFacebookPageRequestTs)
            .let(mapper::apiDataToTs)
            .let { ResponseEntity.ok(it) }
}
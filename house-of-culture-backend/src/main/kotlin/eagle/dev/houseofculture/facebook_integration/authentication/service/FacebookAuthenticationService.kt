package eagle.dev.houseofculture.facebook_integration.authentication.service

import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.FacebookClient.AccessToken
import com.restfb.Parameter
import com.restfb.Version
import com.restfb.exception.devicetoken.FacebookDeviceTokenCodeExpiredException
import com.restfb.exception.devicetoken.FacebookDeviceTokenDeclinedException
import com.restfb.exception.devicetoken.FacebookDeviceTokenPendingException
import com.restfb.exception.devicetoken.FacebookDeviceTokenSlowdownException
import com.restfb.scope.FacebookPermissions
import com.restfb.scope.ScopeBuilder
import com.restfb.types.DeviceCode
import com.restfb.types.Page
import com.restfb.types.User
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.InternalServerError
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.facebook_integration.authentication.model.FacebookApiData
import eagle.dev.houseofculture.facebook_integration.authentication.model.IntegrationStatus
import eagle.dev.houseofculture.facebook_integration.authentication.repository.FacebookApiDataRepository
import eagle.dev.houseofculture.openapi.model.ConnectFacebookPageRequestTs
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import kotlin.concurrent.thread

@Service
class FacebookAuthenticationService(
    private val facebookClient: FacebookClient,
    private val facebookApiDataRepository: FacebookApiDataRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val appPermissions = listOf(
        FacebookPermissions.EMAIL,
        FacebookPermissions.PUBLIC_PROFILE,
        // pages
        FacebookPermissions.PAGES_SHOW_LIST,
        FacebookPermissions.PAGES_MANAGE_ENGAGEMENT,
        FacebookPermissions.PAGES_MANAGE_POSTS,
        FacebookPermissions.PAGES_READ_ENGAGEMENT,
        FacebookPermissions.PAGES_READ_USER_CONTENT,
        FacebookPermissions.PAGES_MANAGE_METADATA,
        FacebookPermissions.PAGES_MANAGE_ADS,
        FacebookPermissions.BUSINESS_MANAGEMENT,
        // instagram
        FacebookPermissions.INSTAGRAM_BASIC,
        FacebookPermissions.INSTAGRAM_CONTENT_PUBLISH
    )

    // table contains single record only
    fun getApiData(): FacebookApiData =
        facebookApiDataRepository.findAll().firstOrNull()
            ?: throw ObjectNotFoundException("No user is connected to application")

    fun generateUserCode(): DeviceCode {
        val scope = ScopeBuilder()
        appPermissions.forEach { scope.addPermission(it) }

        val deviceCode = facebookClient.fetchDeviceCode(scope)

        // runs fetching for access code in new thread
        // TODO: run all requests to fetch in dedicated thread (or figure out something better)
        thread { fetchDeviceAccessToken(deviceCode.code) }

        return deviceCode
    }

    // send requests to check device access token status (and fetch it)
    private fun fetchDeviceAccessToken(code: String) {
        var requestsLeft = NUMBER_OF_REQUESTS
        var accessToken: AccessToken? = null

        while (requestsLeft > 0) {
            try {
                accessToken = facebookClient.obtainDeviceAccessToken(code)
                logger.info("New device access token for Facebook API was generated. Saving token to database.")
                break
            } catch (e: Exception) {
                when (e) {
                    // just still send requests
                    is FacebookDeviceTokenPendingException,
                    is FacebookDeviceTokenSlowdownException -> requestsLeft--
                    // something went wrong, stop requesting
                    is FacebookDeviceTokenCodeExpiredException -> {
                        logger.info("User didn't generate FB device access token with code $code")
                        return
                    }

                    is FacebookDeviceTokenDeclinedException -> {
                        logger.error("Request for FB device token was declined. Cause: ${e.cause}. Message: ${e.message}")
                        return
                    }

                    else -> {
                        logger.error("Error when requesting for device access token. Message: ${e.message}")
                        return
                    }
                }
            }

            sleep(REQUEST_FREQUENCY)
        }

        accessToken?.let {
            saveNewUserAccessToken(it)
        } ?: logger.info("User didn't generate FB device access token with code $code")
    }

    private fun saveNewUserAccessToken(token: AccessToken) {
        // delete old token
        facebookApiDataRepository.deleteAll()

        // save new token
        facebookApiDataRepository.save(
            FacebookApiData(
                IntegrationStatus.ACTIVE,
                token.accessToken,
                getAuthenticatedUserName(token.accessToken),
                token.expires.toInstant()
            )
        )
    }

    private fun getAuthenticatedUserName(userAccessToken: String): String {
        return DefaultFacebookClient(userAccessToken, Version.VERSION_18_0).fetchObject(
            "/me",
            User::class.java,
            Parameter.with("field", "id,name")
        )?.name ?: throw InternalServerError("Cannot fetch user after generating token.")
    }

    fun getAllUserPages(apiData: FacebookApiData? = null): List<Page> {
        val userAccessToken = apiData?.userAccessToken
            ?: try {
                getApiData().userAccessToken
            } catch (e: ObjectNotFoundException) {
                throw ConflictException(
                    "User access token is not generated. Connect application with your Facebook user account first."
                )
            }

        val client = DefaultFacebookClient(userAccessToken, Version.LATEST)

        return client.fetchConnection(
            "/me/accounts/",
            Page::class.java,
            Parameter.with("fields", "id,name,access_token,instagram_business_account{username}")
        ).flatMap { it }
    }

    fun connectFacebookPage(request: ConnectFacebookPageRequestTs): FacebookApiData {
        val apiData = try {
            getApiData()
        } catch (e: ObjectNotFoundException) {
            throw UnprocessableEntityException("User access token hasn't been generated yet.")
        }

        val selectedPage = getAllUserPages(apiData)
            .firstOrNull { it.id == request.pageId } ?: throw ConflictException(
                "Selected page (ID: ${request.pageId} doesn't exist. Page may been deleted/disconnected from user account."
            )

        if(request.instagramId != null && selectedPage.instagramBusinessAccount?.id != request.instagramId)
            throw ConflictException(
                "Instagram account (ID: ${request.instagramId} is not connected with page (ID: ${request.pageId}"
            )

        apiData.pageAccessToken = selectedPage.accessToken
        apiData.pageId = selectedPage.id
        apiData.pageName = selectedPage.name

        if(request.instagramId != null) {
            apiData.instagramProfileId = selectedPage.instagramBusinessAccount.id
            apiData.instagramUsername = selectedPage.instagramBusinessAccount.username
        } else {
            apiData.instagramProfileId = null
            apiData.instagramUsername = null
        }

        return facebookApiDataRepository.save(apiData)
    }

    fun getPageFacebookClient(): DefaultFacebookClient {
        val token = getApiData().pageAccessToken
            ?: throw ConflictException("Facebook page is not connected to application.")
        return DefaultFacebookClient(token, Version.VERSION_18_0)
    }

    companion object {
        const val REQUEST_FREQUENCY = 5100L // fb api requires to wait at least 5s between requests (value in ms)
        const val NUMBER_OF_REQUESTS = 60
    }
}
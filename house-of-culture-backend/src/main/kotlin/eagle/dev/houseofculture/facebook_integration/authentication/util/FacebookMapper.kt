package eagle.dev.houseofculture.facebook_integration.authentication.util

import com.restfb.types.DeviceCode
import com.restfb.types.Page
import com.restfb.types.instagram.IgUser
import eagle.dev.houseofculture.facebook_integration.authentication.model.FacebookApiData
import eagle.dev.houseofculture.openapi.model.FacebookAuthenticationDataResponseTs
import eagle.dev.houseofculture.openapi.model.FacebookPageResponseTs
import eagle.dev.houseofculture.openapi.model.FacebookUserCodeResponseTs
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring")
@JvmDefaultWithCompatibility
interface FacebookMapper {
    @Mapping(target = "code", source = "userCode")
    fun deviceCodeToTs(code: DeviceCode): FacebookUserCodeResponseTs

    @Mapping(target = "pageHref", source = "pageId", qualifiedByName = ["buildFacebookHref"])
    @Mapping(target = "instagramHref", source = "instagramUsername", qualifiedByName = ["buildInstagramHref"])
    fun apiDataToTs(apiData: FacebookApiData): FacebookAuthenticationDataResponseTs

    @Mapping(target = "instagramUsername", source = "instagramBusinessAccount.username")
    @Mapping(target = "instagramId", source = "instagramBusinessAccount.id")
    @Mapping(target = "hasInstagramConnected", source = "instagramBusinessAccount", qualifiedByName = ["hasInstagramConnected"])
    fun pageToTs(page: Page): FacebookPageResponseTs

    @Named("hasInstagramConnected")
    fun hasInstagramConnected(igUser: IgUser?): Boolean = igUser != null

    @Named("buildFacebookHref")
    fun buildFacebookProfileHref(id: String?): String = "https://www.facebook.com/profile.php?id=$id"

    @Named("buildInstagramHref")
    fun buildInstagramHref(username: String?): String = "https://www.instagram.com/$username/"
}
package eagle.dev.houseofculture.post.util

import eagle.dev.houseofculture.category.util.CategoryMapper
import eagle.dev.houseofculture.openapi.model.PostResponseTs
import eagle.dev.houseofculture.openapi.model.PostSummaryResponseTs
import eagle.dev.houseofculture.post.model.Post
import eagle.dev.houseofculture.util.mapper.CommonMapper
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named


@JvmDefaultWithCompatibility // needed to use java "public default" scope on methods
@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = [CategoryMapper::class, CommonMapper::class]
)
interface PostMapper {
    @Mapping(target = "createdAt", qualifiedByName = ["instantToOffsetDateTime"])
    @Mapping(target = "image", source = "image")
    @Mapping(target = "description", source = "post.description", qualifiedByName = ["truncateDescription"])
    @Mapping(target = "fbPostUrl", source = "post.fbPostId", qualifiedByName = ["buildFacebookUrlFromId"])
    @Mapping(target = "igPostUrl", source = "post.igPostId")
    fun postToPostSummaryResponseTs(post: Post, image: String): PostSummaryResponseTs

    @Named("truncateDescription")
    fun truncateDescription(description: String): String {
        val cleanedDescription = description.replace(Regex("<[^>]*>"), "")

        val truncatedText = if (cleanedDescription.length > 200) {
            val lastSpaceIndex = cleanedDescription.lastIndexOf(' ', 200)
            if (lastSpaceIndex > 1) {
                cleanedDescription.substring(0, lastSpaceIndex)
            } else {
                cleanedDescription.substring(0, 200)
            }
        } else {
            cleanedDescription
        }

        return "$truncatedText..."
    }

    @Mapping(target = "createdAt", qualifiedByName = ["instantToOffsetDateTime"])
    @Mapping(target = "images", source = "images")
    @Mapping(target = "fbPostUrl", source = "post.fbPostId", qualifiedByName = ["buildFacebookUrlFromId"])
    @Mapping(target = "igPostUrl", source = "post.igPostId")
    fun postToPostResponseTs(post: Post, images: List<String>): PostResponseTs

    @Named("buildFacebookUrlFromId")
    fun buildFacebookUrlFromId(id: String?) = id?.let { "https://facebook.com/${id}" }
}

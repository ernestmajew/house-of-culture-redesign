package eagle.dev.houseofculture.post.service

import eagle.dev.houseofculture.exceptions.InternalServerError
import eagle.dev.houseofculture.facebook_integration.content.service.FacebookContentService
import eagle.dev.houseofculture.facebook_integration.content.service.InstagramContentService
import eagle.dev.houseofculture.image.service.ImageService
import eagle.dev.houseofculture.post.model.Post
import eagle.dev.houseofculture.post.repository.PostRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class PostSocialMediaService(
    private val facebookContentService: FacebookContentService,
    private val instagramContentService: InstagramContentService,
    private val imageService: ImageService,
    private val postRepository: PostRepository,
) {
    fun createSocialMediaPosts(post: Post, createFb: Boolean, createIg: Boolean) {
        val description = buildSocialMediaPostDescription(post)
        val images = imageService.getAllImages("/posts/${post.id}").map(imageService::getImage)

        try {
            runBlocking {
                val fbDeferred = if (createFb) {
                    async { facebookContentService.createPost(description, images) }
                } else null

                val igDeferred = if (createIg) {
                    async { instagramContentService.createPost(description, images) }
                } else null

                post.fbPostId = fbDeferred?.await()
                post.igPostId = igDeferred?.await()
            }

            postRepository.save(post)
        } catch (e: Exception) {
            throw InternalServerError(e.message ?: "")
        }
    }

    fun updateSocialMediaPosts(post: Post, updateFb: Boolean, updateIg: Boolean) {
        deleteSocialMediaPosts(post, updateFb, updateIg)

        createSocialMediaPosts(post, updateFb, updateIg)
    }

    fun deleteSocialMediaPosts(post: Post, deleteFb: Boolean = true, deleteIg: Boolean = true): Post {
        if(deleteFb && post.fbPostId != null) {
            facebookContentService.deletePost(post.fbPostId!!)
            post.fbPostId = null
        }

        // Instagram API doesn't support deleting and updating posts, so we only detach url from entity
        // https://developers.facebook.com/docs/instagram-api/reference/ig-user/media#updating
        if(deleteIg && post.igPostId != null) {
            post.igPostId = null
        }

        return post
    }

    private fun buildSocialMediaPostDescription(post: Post): String {
        val descriptionWithoutHtml = post.description
            .replace(Regex("</.*?>"), "\n")
            .replace(Regex("<.*?>"), "")

        return "${post.title}\n\n${descriptionWithoutHtml}"
    }

}
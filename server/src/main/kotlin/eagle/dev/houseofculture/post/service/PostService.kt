package eagle.dev.houseofculture.post.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.category.service.CategoryService
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.image.service.ImageService
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.post.model.Post
import eagle.dev.houseofculture.post.repository.PostRepository
import eagle.dev.houseofculture.post.util.PostMapper
import eagle.dev.houseofculture.util.toPageRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val categoryService: CategoryService,
    private val imageService: ImageService,
    private val postSocialMediaService: PostSocialMediaService,
    private val authService: AuthService,
    private val mapper: PostMapper,
) {
    val postsPath = "posts"

    fun getPostResponsesWithPaginationAndCategory(
        request: MinimalPageableTs?,
        categoryId: Long?
    ): PaginatedPostResponseTs {
        // by default return first 6 posts
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageable = request?.toPageRequest(sort) ?: PageRequest.of(0, 6, sort)

        val posts = if (categoryId != null) {
            postRepository.findAllByCategoriesId(categoryId, pageable)
        } else {
            postRepository.findAll(pageable)
        }

        val postList = posts.content.map { post ->
            val image = imageService.getFirstImage(post.getImagesDirectory())
            mapper.postToPostSummaryResponseTs(post, image)
        }

        val pageNumber = posts.totalPages;
        return PaginatedPostResponseTs(postList, pageNumber)
    }

    fun savePost(request: CreatePostRequestTs): PostSummaryResponseTs {
        val user = authService.loggedInUserOrException()

        val categories = try {
            val categoryIds = request.categories ?: emptyList()
            categoryService.findByIds(categoryIds)
        } catch (e: ObjectNotFoundException) {
            throw UnprocessableEntityException(e.message)
        }

        val savedPost = postRepository.save(Post(request.title, request.description, categories, user))

        val savedImages = savePostImages(
            savedPost,
            request.images ?: emptyList()
        )

        if(request.createFbPost || request.createIgPost)
            postSocialMediaService.createSocialMediaPosts(savedPost, request.createFbPost, request.createIgPost)

        return mapper.postToPostSummaryResponseTs(savedPost, savedImages)
    }

    fun getPostById(id: Long): PostResponseTs {
        val post = postRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("There is no post with id $id") }

        val images = imageService.getAllImages(post.getImagesDirectory())
        return mapper.postToPostResponseTs(post, images)
    }

    fun editPost(id: Long, editPostRequestTs: CreatePostRequestTs): PostSummaryResponseTs {
        val existingPost = postRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("There is no post with id $id") }

        existingPost.title = editPostRequestTs.title
        existingPost.description = editPostRequestTs.description

        editPostRequestTs.categories?.let { newCategoryIds ->
            try {
                val existingCategories = categoryService.findByIds(newCategoryIds)
                existingPost.categories = existingCategories.toMutableList()
            } catch (e: ObjectNotFoundException) {
                throw UnprocessableEntityException(e.message)
            }
        }

        val savedPost = postRepository.save(existingPost)

        val savedImages = savePostImages(
            savedPost,
            editPostRequestTs.images ?: emptyList()
        )

        if(editPostRequestTs.createFbPost || editPostRequestTs.createIgPost)
            postSocialMediaService.updateSocialMediaPosts(
                savedPost,
                editPostRequestTs.createFbPost,
                editPostRequestTs.createIgPost
            )

        return mapper.postToPostSummaryResponseTs(savedPost, savedImages)
    }

    // return False if social media posts cannot be deleted
    fun deletePost(id: Long): Boolean {
        val post = postRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("There is no post with id $id") }

        postSocialMediaService.deleteSocialMediaPosts(post)

        val deleteSocialMediaResult = runCatching {
            deletePostImages(post.getImagesDirectory())
        }

        postRepository.delete(post)

        return deleteSocialMediaResult.isSuccess
    }

    private fun deletePostImages(path: String) {
        imageService.clearDirectory(path) // delete images and directory
    }

    private fun savePostImages(post: Post, images: List<String>): String {
        saveImages(images, post.getImagesDirectory())
        return imageService.getFirstImage(post.getImagesDirectory())
    }

    private fun saveImages(images: List<String>, path: String) {
        imageService.clearDirectory(path)
        images.mapIndexed { index, it ->
            imageService.saveImage(it, path, "image_$index")
        }
    }

    private fun Post.getImagesDirectory() =
        "$postsPath/$id"
}
package eagle.dev.houseofculture.post.controller

import eagle.dev.houseofculture.openapi.api.PostApi
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.post.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional
class PostController(
    private val postService: PostService
) : PostApi {
    override fun createPost(createPostRequestTs: CreatePostRequestTs): ResponseEntity<PostSummaryResponseTs> =
        postService.savePost(createPostRequestTs)
            .let { ResponseEntity.ok(it) }

    // use custom request instead of Pageable because OpenApiGenerator cannot use it as params (IDK why)
    override fun getPostsWithPagination(paginationParams: MinimalPageableTs?, categoryId: Long?): ResponseEntity<PaginatedPostResponseTs> {
        val paginatedPosts = postService.getPostResponsesWithPaginationAndCategory(paginationParams, categoryId)
        return ResponseEntity.ok(paginatedPosts)
    }

    override fun getPostById(id: Long): ResponseEntity<PostResponseTs> =
        postService.getPostById(id)
            .let { ResponseEntity.ok(it) }

    override fun editPost(id: Long, createPostRequestTs: CreatePostRequestTs): ResponseEntity<PostSummaryResponseTs> =
        postService.editPost(id, createPostRequestTs)
            .let { ResponseEntity.ok(it) }

    override fun deletePost(id: Long): ResponseEntity<Unit> {
        postService.deletePost(id)
        return noContentResponse()
    }

    private fun noContentResponse(): ResponseEntity<Unit> {
        return ResponseEntity.noContent().build()
    }
}
package eagle.dev.houseofculture.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import eagle.dev.houseofculture.category.service.CategoryService
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.post.service.PostService
import eagle.dev.houseofculture.util.TestSecurityConfiguration
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.OffsetDateTime

@ActiveProfiles("test")
@ContextConfiguration(classes = [TestSecurityConfiguration::class])
@WebMvcTest(PostController::class)
class PostControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var categoryService: CategoryService

    private val TEST_ID: Long = 1L
    private val TEST_VALUE: String = "TEST"
    private val TEST_IMAGE: String = "image1.jpg"
    private val PAGE: Int = 1
    private val PAGE_NUMBER: Int = 10
    private val EDIT_TEST_VALUE: String = "TEST"
    private val EDIT_TEST_IMAGE: String = "edit1.jpg"


    private val POST_URL: String = "/api/post"
    private val PUBLIC_POST_URL: String = "/api/public/post"
    private val PUBLIC_POST_ID_URL: String = "/api/public/post/1"
    private val POST_ID_URL: String = "/api/post/1"

    // get post constance
    private val IMAGE_VALUE_FIELD: String = "$.images[0]"

    // create post constance
    private val ID_FIELD: String = "$.id"
    private val TITLE_FIELD: String = "$.title"
    private val DESCRIPTION_FIELD: String = "$.description"
    private val CATEGORIES_NAME_FIELD: String = "$.categories[0].name"
    private val IMAGE_FIELD: String = "$.image"

    // get posts constance
    private val POSTS_LIST: String = "$.posts"
    private val POST_ID: String = "$.posts[0].id"
    private val POST_TITLE: String = "$.posts[0].title"

    // error
    private val MESSAGE: String = "$.message"
    private val UNPROCESSABLE_ENTITY_ERROR: String = "422 UNPROCESSABLE_ENTITY"
    private val NOT_FOUND_ERROR: String = "404 NOT_FOUND"

    private lateinit var postSummary: PostSummaryResponseTs
    private lateinit var categoryResponse: CategoryResponseTs
    private lateinit var userTs: UserTs

    @BeforeEach
    fun setUp() {
        categoryResponse = CategoryResponseTs(name = TEST_VALUE, id = TEST_ID)

        postSummary = PostSummaryResponseTs(
            id = TEST_ID,
            title = TEST_VALUE,
            description = TEST_VALUE,
            categories = listOf(categoryResponse),
            createdAt = OffsetDateTime.now(),
            image = TEST_IMAGE
        )

        userTs = UserTs(
            id = TEST_ID,
            firstName = TEST_VALUE,
            lastName = TEST_VALUE,
            birthdate = LocalDate.now(),
            email = TEST_VALUE,
            getsNewsletter = true,
            role = UserRoleTs.ADMIN
        )
    }

    @Nested
    inner class CreatePost {
        @Test
        fun testCreatePostReturnIsCreated() {
            // Given
            val createPostRequest = createPostRequest()

            // When
            `when`(postService.savePost(createPostRequest)).thenReturn(postSummary)

            // Then
            mockMvc.perform(
                post(POST_URL)
                    .content(objectMapper.writeValueAsString(createPostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_FIELD).exists())
                .andExpect(jsonPath(TITLE_FIELD).value(TEST_VALUE))
                .andExpect(jsonPath(DESCRIPTION_FIELD).value(TEST_VALUE))
                .andExpect(jsonPath(CATEGORIES_NAME_FIELD).value(TEST_VALUE))
                .andExpect(jsonPath(IMAGE_FIELD).value(TEST_IMAGE))
        }

        @Test
        fun testCreatePostWithUnprocessableEntityException() {
            // Given
            val createPostRequest = createPostRequest()

            // When
            `when`(postService.savePost(createPostRequest)).thenThrow(UnprocessableEntityException(""))

            // Then
            mockMvc.perform(
                post(POST_URL)
                    .content(objectMapper.writeValueAsString(createPostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath(MESSAGE, containsString(UNPROCESSABLE_ENTITY_ERROR)))
        }

        private fun createPostRequest(): CreatePostRequestTs {
            return CreatePostRequestTs(
                title = TEST_VALUE,
                description = TEST_VALUE,
                categories = listOf(TEST_ID),
                createFbPost = true,
                createIgPost = false,
                images = listOf(TEST_IMAGE)
            )
        }
    }

    @Nested
    inner class GetPostsWithPagination {
        @Test
        fun testGetPostsWithPagination() {
            // Given
            val paginationParams = MinimalPageableTs(page = PAGE, pageSize = PAGE_NUMBER)
            val paginatedPosts = createPaginatedPosts()

            // When
            `when`(postService.getPostResponsesWithPaginationAndCategory(paginationParams, TEST_ID)).thenReturn(
                paginatedPosts
            )

            // Then
            mockMvc.perform(
                get(PUBLIC_POST_URL)
                    .param("page", PAGE.toString())
                    .param("pageSize", PAGE_NUMBER.toString())
                    .param("categoryId", TEST_ID.toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath(POSTS_LIST, hasSize<Any>(paginatedPosts.posts.size)))
                .andExpect(jsonPath(POST_ID).value(paginatedPosts.posts[0].id))
                .andExpect(jsonPath(POST_TITLE).value(paginatedPosts.posts[0].title))

            verify(postService).getPostResponsesWithPaginationAndCategory(paginationParams, TEST_ID)
        }

        private fun createPaginatedPosts(): PaginatedPostResponseTs {
            return PaginatedPostResponseTs(
                posts = listOf(postSummary),
                numberOfPages = PAGE
            )
        }
    }

    @Nested
    inner class GetPostById {
        @Test
        fun testGetPostById() {
            // Given
            val postResponse = createPostResponse()

            // When
            `when`(postService.getPostById(TEST_ID)).thenReturn(postResponse)

            // Then
            mockMvc.perform(
                get(PUBLIC_POST_ID_URL)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_FIELD).value(postResponse.id))
                .andExpect(jsonPath(TITLE_FIELD).value(postResponse.title))
                .andExpect(jsonPath(DESCRIPTION_FIELD).value(TEST_VALUE))
                .andExpect(jsonPath(CATEGORIES_NAME_FIELD).value(TEST_VALUE))
                .andExpect(jsonPath(IMAGE_VALUE_FIELD).value(TEST_IMAGE))

            verify(postService).getPostById(TEST_ID)
        }

        @Test
        fun testGetPostByIdWithObjectNotFoundException() {
            // Given
            val postResponse = createPostResponse()

            // When
            `when`(postService.getPostById(TEST_ID)).thenThrow(ObjectNotFoundException(""))

            // Then
            mockMvc.perform(
                get(PUBLIC_POST_ID_URL)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE, containsString(NOT_FOUND_ERROR)))
        }

        private fun createPostResponse(): PostResponseTs {
            return PostResponseTs(
                id = TEST_ID,
                title = TEST_VALUE,
                description = TEST_VALUE,
                categories = listOf(categoryResponse),
                author = userTs,
                createdAt = OffsetDateTime.now(),
                images = listOf(TEST_IMAGE)
            )
        }
    }

    @Nested
    inner class EditPost {
        @Test
        fun testEditPost() {
            // Given
            val editPostRequest = createEditPostRequest()
            val editedPostSummary = createEditedPostSummary()

            // When
            `when`(postService.editPost(TEST_ID, editPostRequest)).thenReturn(editedPostSummary)

            // Then
            mockMvc.perform(
                put(POST_ID_URL)
                    .content(objectMapper.writeValueAsString(editPostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_FIELD).value(TEST_ID))
                .andExpect(jsonPath(TITLE_FIELD).value(EDIT_TEST_VALUE))
                .andExpect(jsonPath(DESCRIPTION_FIELD).value(EDIT_TEST_VALUE))
                .andExpect(jsonPath(CATEGORIES_NAME_FIELD).value(TEST_VALUE))
                .andExpect(jsonPath(IMAGE_FIELD).value(EDIT_TEST_IMAGE))

            verify(postService).editPost(TEST_ID, editPostRequest)
        }

        @Test
        fun testEditPostWithObjectNotFoundException() {
            // Given
            val editPostRequest = createEditPostRequest()

            // When
            `when`(postService.editPost(TEST_ID, editPostRequest)).thenThrow(ObjectNotFoundException(""))

            // Then
            mockMvc.perform(
                put(POST_ID_URL)
                    .content(objectMapper.writeValueAsString(editPostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE, containsString(NOT_FOUND_ERROR)))

        }

        private fun createEditPostRequest(): CreatePostRequestTs {
            return CreatePostRequestTs(
                title = EDIT_TEST_VALUE,
                description = EDIT_TEST_VALUE,
                categories = listOf(TEST_ID),
                createFbPost = false,
                createIgPost = true,
                images = listOf(EDIT_TEST_IMAGE)
            )
        }

        private fun createEditedPostSummary(): PostSummaryResponseTs {
            return PostSummaryResponseTs(
                id = TEST_ID,
                title = EDIT_TEST_VALUE,
                description = EDIT_TEST_VALUE,
                categories = listOf(categoryResponse),
                createdAt = OffsetDateTime.now(),
                image = EDIT_TEST_IMAGE
            )
        }
    }

    @Nested
    inner class DeletePost {
        @Test
        fun testDeletePost() {

            // Then
            mockMvc.perform(
                delete(POST_ID_URL)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNoContent())

            verify(postService).deletePost(TEST_ID)
        }

        @Test
        fun testDeletePostWithObjectNotFoundException() {

            // When
            `when`(postService.deletePost(TEST_ID)).thenThrow(ObjectNotFoundException(""))

            // Then
            mockMvc.perform(
                delete(POST_ID_URL)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE, containsString(NOT_FOUND_ERROR)))

            verify(postService).deletePost(TEST_ID)
        }
    }

}
package eagle.dev.houseofculture.post.service

import eagle.dev.houseofculture.auth.service.AuthService
import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.category.service.CategoryService
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.image.service.ImageService
import eagle.dev.houseofculture.openapi.model.*
import eagle.dev.houseofculture.post.model.Post
import eagle.dev.houseofculture.post.repository.PostRepository
import eagle.dev.houseofculture.post.util.PostMapper
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import org.springframework.data.domain.Sort
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class PostServiceTest {
    private lateinit var postService: PostService
    private val postRepository: PostRepository = mockk()
    private val categoryService: CategoryService = mockk()
    private val imageService: ImageService = mockk()
    private val authService: AuthService = mockk()
    private val mapper: PostMapper = mockk()
    private val postSocialMediaService: PostSocialMediaService = mockk()

    private val TEST_ID: Long = 1L
    private val TEST_VALUE: String = "TEST"
    private val TEST_IMAGE: String = "image1.jpg"
    private val FACEBOOK: String = "https://www.facebook.com/"
    private val INSTAGRAM: String = "https://www.instagram.com/"
    private val PATH: String = "posts/$TEST_ID"
    private val SORT = Sort.by(Sort.Direction.DESC, "createdAt")

    private lateinit var request: MinimalPageableTs
    private lateinit var post: Post
    private lateinit var user: Client
    private lateinit var userTs: UserTs
    private lateinit var image: String
    private lateinit var postSummary: PostSummaryResponseTs
    private lateinit var postResponse: PostResponseTs
    private lateinit var createPostRequest: CreatePostRequestTs
    private lateinit var category: Category
    private lateinit var categoryResponse: CategoryResponseTs

    @BeforeEach
    fun setUp() {
        postService = PostService(postRepository, categoryService,imageService, postSocialMediaService, authService, mapper)

        request = MinimalPageableTs(0, 6)

        user = Client(
            password = TEST_VALUE,
            role = UserRole.ADMIN,
            status = UserStatus.ACTIVE,
            email = TEST_VALUE,
            firstName = TEST_VALUE,
            lastName = TEST_VALUE,
            birthdate = LocalDate.now()
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
        post = Post(
            title = TEST_VALUE,
            description = TEST_VALUE,
            author = user,
            categories = mutableListOf(),
            fbPostId = null,
            igPostId = INSTAGRAM
        )
        image = TEST_VALUE
        postSummary = PostSummaryResponseTs(
            id = TEST_ID,
            title = TEST_VALUE,
            description = TEST_VALUE,
            createdAt = OffsetDateTime.now(),
            image = TEST_IMAGE
        )

        category = Category(TEST_VALUE)
        category.id = TEST_ID

        createPostRequest = CreatePostRequestTs(
            title = TEST_VALUE,
            description = TEST_VALUE,
            categories = listOf(TEST_ID),
            createFbPost = true,
            createIgPost = false,
            images = listOf(TEST_IMAGE)
        )

        categoryResponse = CategoryResponseTs(name = TEST_VALUE, id = TEST_ID)

        postResponse = PostResponseTs(
            id = TEST_ID,
            title = TEST_VALUE,
            description = TEST_VALUE,
            categories = listOf(categoryResponse),
            author = userTs,
            createdAt = OffsetDateTime.now(),
            images = listOf(TEST_IMAGE)
        )
    }

    @Nested
    inner class GetPostResponsesWithPaginationAndCategory {
        @Test
        fun testGetPostResponsesWithPaginationAndCategory() {
            // Given
            val pageable = PageRequest.of(0, 6, SORT)
            val paginatedPosts = PageImpl(listOf(post))

            every { postRepository.findAllByCategoriesId(TEST_ID, pageable) } returns paginatedPosts
            every { postRepository.findAll(pageable) } returns paginatedPosts
            every { imageService.getFirstImage(any()) } returns image
            every { mapper.postToPostSummaryResponseTs(post, image) } returns postSummary

            // When
            val result = postService.getPostResponsesWithPaginationAndCategory(request, TEST_ID)

            // Then
            verify { postRepository.findAllByCategoriesId(TEST_ID, pageable) }
            verify { mapper.postToPostSummaryResponseTs(post, image) }
            verify { imageService.getFirstImage(any()) }

            assertEquals(result.posts.size, 1)
            assertEquals(result.posts[0].id, TEST_ID)
        }

        @Test
        fun testGetPostResponsesWithPaginationAndCategoryWhenCategoryIdIsNull() {
            // Given
            val pageable = PageRequest.of(0, 6, SORT)
            val paginatedPosts = PageImpl(listOf(post))

            every { postRepository.findAll(pageable) } returns paginatedPosts
            every { imageService.getFirstImage(any()) } returns image
            every { mapper.postToPostSummaryResponseTs(post, image) } returns postSummary

            // When
            val result = postService.getPostResponsesWithPaginationAndCategory(request, null)

            // Then
            verify { postRepository.findAll(pageable) }
            verify { imageService.getFirstImage(any()) }
            verify { mapper.postToPostSummaryResponseTs(post, image) }

            assertEquals(result.posts.size, 1)
            assertEquals(result.posts[0].id, TEST_ID)
        }
    }

    @Nested
    inner class SavePost {
        @Nested
        inner class SavePost {
            @Test
            fun testSavePost() {
                // Given
                val categories = listOf(category)

                every { authService.loggedInUserOrException() } returns user
                every { categoryService.findByIds(listOf(TEST_ID)) } returns categories
                every { postRepository.save(any()) } answers { post }
                every { imageService.saveImage(any(), any(), any()) } returns PATH
                every { imageService.getFirstImage(any()) } returns TEST_IMAGE
                every { postRepository.delete(any()) } just runs
                every { imageService.clearDirectory(any()) } just runs
                every { mapper.postToPostSummaryResponseTs(any(), any()) } answers { postSummary }
                every { postSocialMediaService.createSocialMediaPosts(post, any(), any())} just runs

                // When
                val result = postService.savePost(createPostRequest)

                // Then
                verify { authService.loggedInUserOrException() }
                verify { categoryService.findByIds(listOf(TEST_ID)) }
                verify { postRepository.save(any()) }
                verify(exactly = 1) { imageService.saveImage(any(), any(), any()) }
                verify { mapper.postToPostSummaryResponseTs(any(), any()) }
                verify { postSocialMediaService.createSocialMediaPosts(post, any(), any())}

                assertNotNull(result)
                assertEquals(TEST_ID, result.id)
                assertEquals(createPostRequest.title, result.title)
                assertEquals(createPostRequest.description, result.description)
                assertNotNull(result.createdAt)
            }

            @Test
            fun testSavePostWithImageProcessingError() {
                // Given
                val categories = listOf(category)

                every { authService.loggedInUserOrException() } returns user
                every { categoryService.findByIds(listOf(TEST_ID)) } throws UnprocessableEntityException("")

                // When
                val result = assertThrows<UnprocessableEntityException> {
                    postService.savePost(createPostRequest)
                }

                // Then
                verify { authService.loggedInUserOrException() }
                verify { categoryService.findByIds(listOf(TEST_ID)) }

                assertTrue(result.message.contains("422 UNPROCESSABLE_ENTITY"))
            }
        }
    }

    @Nested
    inner class GetPostById {
        @Test
        fun testGetPostByIdNotFound() {
            // Given
            every { postRepository.findById(TEST_ID) } returns Optional.empty()

            // When
            val exception = assertThrows<ObjectNotFoundException> {
                postService.getPostById(TEST_ID)
            }

            // Then
            verify { postRepository.findById(TEST_ID) }

            assertTrue(exception.message.contains("There is no post with id $TEST_ID"))
        }

        @Test
        fun testGetPostByIdFound() {
            // Given
            val imagePaths = listOf(TEST_IMAGE)

            every { postRepository.findById(TEST_ID) } returns Optional.of(post)
            every { imageService.getAllImages(any()) } returns imagePaths
            every { mapper.postToPostResponseTs(post, imagePaths) } returns postResponse

            // When
            val result = postService.getPostById(TEST_ID)

            // Then
            verify { postRepository.findById(TEST_ID) }
            verify { mapper.postToPostResponseTs(post, imagePaths) }

            assertEquals(TEST_ID, result.id)
            assertEquals(TEST_VALUE, result.title)
            assertEquals(TEST_VALUE, result.description)
            assertEquals(imagePaths, result.images)
        }
    }

    @Nested
    inner class EditPost {
        @Test
        fun testEditPost() {
            // Given
            val updatedPost = post.copy()
            updatedPost.title = createPostRequest.title
            updatedPost.description = createPostRequest.description
            updatedPost.categories = listOf(category).toMutableList()
            updatedPost.fbPostId = FACEBOOK
            updatedPost.igPostId = INSTAGRAM
            val postArg = slot<Post>()
            val imagePathArg = slot<String>()

            every { postRepository.findById(TEST_ID) } returns Optional.of(post)
            every { postRepository.save(capture(postArg)) } answers { postArg.captured }
            every { categoryService.findByIds(listOf(TEST_ID)) } returns listOf(category)
            every { imageService.saveImage(any(), any(), capture(imagePathArg)) } returns PATH
            every { imageService.getFirstImage(any()) } returns TEST_IMAGE
            every { imageService.clearDirectory(any()) } just runs
            every { mapper.postToPostSummaryResponseTs(any(), any()) } answers { postSummary }
            every { postSocialMediaService.updateSocialMediaPosts(post, any(), any()) } just runs

            // When
            val result = postService.editPost(TEST_ID, createPostRequest)

            // Then
            verify { postRepository.findById(TEST_ID) }
            verify(exactly = 1) { postRepository.save(any()) }
            verify { categoryService.findByIds(listOf(TEST_ID)) }
            verify(exactly = 1) { imageService.saveImage(any(), any(), any()) }
            verify { mapper.postToPostSummaryResponseTs(any(), any()) }

            assertNotNull(result)
            assertEquals(TEST_ID, result.id)
            assertEquals(updatedPost.title, result.title)
            assertEquals(updatedPost.description, result.description)
            assertNotNull(result.createdAt)
            assertEquals(TEST_IMAGE, result.image)
        }

        @Test
        fun testEditPostWithCategoriesNotFound() {
            // Given
            val updatedPost = post.copy()
            updatedPost.title = createPostRequest.title
            updatedPost.description = createPostRequest.description
            updatedPost.categories = listOf(category).toMutableList()
            updatedPost.fbPostId = FACEBOOK
            updatedPost.igPostId = INSTAGRAM

            every { postRepository.findById(TEST_ID) } returns Optional.of(post)
            every { categoryService.findByIds(listOf(TEST_ID)) } throws ObjectNotFoundException("Category not found")
            every { imageService.saveImage(any(), any(), any()) } returns PATH
            every { imageService.getFirstImage(any()) } returns TEST_IMAGE
            every { imageService.clearDirectory(any()) } just runs
            every { postRepository.delete(any()) } just runs
            every { mapper.postToPostSummaryResponseTs(post, TEST_IMAGE) } returns postSummary

            // When
            val exception = assertThrows<UnprocessableEntityException> {
                postService.editPost(TEST_ID, createPostRequest)
            }

            // Then
            verify { postRepository.findById(TEST_ID) }
            verify { categoryService.findByIds(listOf(TEST_ID)) }

            assertTrue(exception.message.contains("Category not found"))
        }
    }

    @Nested
    inner class DeletePost {
        @Test
        fun testDeletePost() {
            // Given
            every { postRepository.findById(TEST_ID) } returns Optional.of(post)
            every { imageService.clearDirectory(any()) } just runs
            every { postRepository.delete(post) } just runs
            every { postSocialMediaService.deleteSocialMediaPosts(post, any(), any()) } returns post

            // When
            postService.deletePost(TEST_ID)

            // Then
            verify { postRepository.findById(TEST_ID) }
            verifyOrder {
                imageService.clearDirectory(any())
                postRepository.delete(post)
            }
        }

        @Test
        fun testDeletePostWithError() {
            // Given
            every { postRepository.findById(TEST_ID) } returns Optional.empty()

            // When
            val exception = assertThrows<ObjectNotFoundException> {
                postService.deletePost(TEST_ID)
            }

            // Then
            verify(exactly = 0) { postRepository.delete(post) }
            assertTrue(exception.message.contains("There is no post with id 1"))
        }
    }
}
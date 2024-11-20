package eagle.dev.houseofculture.post.repository

import eagle.dev.houseofculture.post.model.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long> {
    fun existsByCategoriesId(categoryId: Long): Boolean
    fun findAllByCategoriesId(categoryId: Long, pageable: Pageable): Page<Post>
}
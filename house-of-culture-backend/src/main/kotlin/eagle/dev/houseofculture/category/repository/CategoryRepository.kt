package eagle.dev.houseofculture.category.repository

import eagle.dev.houseofculture.category.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: JpaRepository<Category, Long> {
    fun existsByName(name: String): Boolean
    fun findAllByIdIn(id: Collection<Long>): List<Category>
    fun findByNameContaining(name: String): List<Category>
}
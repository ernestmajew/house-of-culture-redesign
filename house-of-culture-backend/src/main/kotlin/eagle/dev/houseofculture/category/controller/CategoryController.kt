package eagle.dev.houseofculture.category.controller

import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.category.service.CategoryService
import eagle.dev.houseofculture.category.util.CategoryMapper
import eagle.dev.houseofculture.openapi.api.CategoryApi
import eagle.dev.houseofculture.openapi.model.CategoryResponseTs
import eagle.dev.houseofculture.openapi.model.CreateCategoryRequestTs
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional
class CategoryController(
    private val categoryService: CategoryService,
    private val mapper: CategoryMapper
): CategoryApi {
    override fun createCategory(createCategoryRequestTs: CreateCategoryRequestTs): ResponseEntity<CategoryResponseTs> =
        categoryService.saveCategory(createCategoryRequestTs).toResponseOk()

    override fun deleteCategory(id: Long): ResponseEntity<Unit> =
        ResponseEntity.ok(categoryService.deleteById(id))

    override fun findCategoriesByName(name: String): ResponseEntity<List<CategoryResponseTs>> =
        categoryService.findCategoriesByName(name)
            .map(mapper::categoryToTs)
            .let { ResponseEntity.ok(it) }

    private fun Category.toResponseOk() =
        mapper.categoryToTs(this).let { ResponseEntity.ok(it) }
}
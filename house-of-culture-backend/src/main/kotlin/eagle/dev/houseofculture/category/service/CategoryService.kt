package eagle.dev.houseofculture.category.service

import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.category.repository.CategoryRepository
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.openapi.model.CreateCategoryRequestTs
import eagle.dev.houseofculture.post.repository.PostRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CategoryService(
    private val categoriesRepository: CategoryRepository,
    private val postRepository: PostRepository
) {
    fun saveCategory(request: CreateCategoryRequestTs): Category {
        if(categoriesRepository.existsByName(request.name))
            throw ConflictException("Category $request.name already exists.")

        return categoriesRepository.save(Category(request.name))
    }

    fun findByIds(ids: List<Long>): List<Category> {
        val foundCategories = categoriesRepository.findAllByIdIn(ids)

        // if any category was not found throw Exception
        val foundCategoriesIds = foundCategories.map { it.id!! }
        ids.firstOrNull { !foundCategoriesIds.contains(it) }?.let {
            throw ObjectNotFoundException("There is no category with id $it")
        }

        return foundCategories
    }

    fun findByIdOrNull(id: Long): Category? =
        categoriesRepository.findById(id).getOrNull()

    fun findCategoriesByName(name: String): List<Category> =
        categoriesRepository.findByNameContaining(name)

    fun deleteById(id: Long) {
        if(postRepository.existsByCategoriesId(id))
            throw ConflictException("Cannot delete category associated with posts!")

        // TODO: check if category is set in any event

        categoriesRepository.deleteById(id)
    }
}

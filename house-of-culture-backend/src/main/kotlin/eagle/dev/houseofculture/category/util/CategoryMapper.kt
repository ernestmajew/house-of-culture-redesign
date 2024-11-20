package eagle.dev.houseofculture.category.util

import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.openapi.model.CategoryResponseTs
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
@JvmDefaultWithCompatibility
interface CategoryMapper {
    fun categoryToTs(category: Category): CategoryResponseTs
}

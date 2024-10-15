package eagle.dev.houseofculture.category.model

import eagle.dev.houseofculture.post.model.Post
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "category")
data class Category(
    @NotBlank(message = "Name cannot be empty.")
    val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToMany(mappedBy = "categories")
    val posts: Set<Post> = mutableSetOf()
}

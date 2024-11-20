package eagle.dev.houseofculture.post.model

import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.user.model.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "post")
data class Post(
    var title: String,
    var description: String, // stored as HTML

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "post_category",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    var categories: List<Category> = emptyList(),

    @ManyToOne
    @JoinColumn(name = "author_id")
    val author: User,

    var fbPostId: String? = null,
    var igPostId: String? = null,

    val createdAt: Instant? = Instant.now()
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

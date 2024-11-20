package eagle.dev.houseofculture.event.model

import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.user.model.Client
import jakarta.persistence.*

@Entity
@Table(name = "event")
data class Event (
    @Column(nullable = false)
    var title: String,

    @Enumerated(EnumType.STRING)
    val type: EventType,

    var description: String?,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "event_category",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    var categories: List<Category> = emptyList(),

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "instructor_id", nullable = true)
    var instructor: Client? = null, // for events that took place only once field is nullable

    var minimumAge: Int? = null,

    var maximumAge: Int? = null,

    var maxPlaces: Int? = null,

    var cost: Double? = null,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = [CascadeType.ALL])
    var singleEvents: List<SingleEvent> = emptyList(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: EventStatus = EventStatus.ACTIVE
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Transient val isFree: Boolean
        get() = cost == null || cost!!.equals(0.0)

    // available places in the closest single event
    @get:Transient val availablePlaces: Int
        get() = singleEvents.filter { !it.isFromPast }.minByOrNull { it.starts }?.availablePlaces ?: 0

    @get:Transient val numberOfEnrollments: Int
        get() = singleEvents.map { it.enrollments.size }.sum()

}

enum class EventStatus {
    ACTIVE,
    FINISHED,
    CANCELLED
}

enum class EventType {
    SINGLE,
    MULTIPLE
}
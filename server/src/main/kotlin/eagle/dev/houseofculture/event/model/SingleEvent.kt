package eagle.dev.houseofculture.event.model

import eagle.dev.houseofculture.enrollment.model.Enrollment
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "single_event")
data class SingleEvent(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    var starts: Instant,

    var ends: Instant,

    var isCancelled: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "event")
    var enrollments: Set<Enrollment> = emptySet()

    @get:Transient val availablePlaces: Int?
        get() = event.maxPlaces?.minus(enrollments.size)

    @get:Transient val isFromPast: Boolean
        get() = starts.isBefore(Instant.now())
}

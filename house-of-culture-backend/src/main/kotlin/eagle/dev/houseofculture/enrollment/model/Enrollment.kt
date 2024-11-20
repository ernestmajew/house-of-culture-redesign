package eagle.dev.houseofculture.enrollment.model

import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.user.model.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "enrollment")
data class Enrollment(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    var client: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: SingleEvent,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "payment_id")
    var payment: Payment? = null,

    @Column(name = "enrollment_time", nullable = false)
    var enrollmentTime: Instant = Instant.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}


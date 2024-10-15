package eagle.dev.houseofculture.enrollment.model

import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.user.model.User
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity
@Table(name = "old_enrollment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
data class OldEnrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "client_id", nullable = false)
    var clientId: Long,

    @Column(name = "event_id", nullable = false)
    var eventId: Long,

    @Column(name = "enrollment_time", nullable = false)
    var enrollmentTime: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", insertable = false, updatable = false)
    var client: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id", insertable = false, updatable = false)
    var event: SingleEvent
)

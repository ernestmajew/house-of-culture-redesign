package eagle.dev.houseofculture.enrollment.model

import eagle.dev.houseofculture.user.model.Client
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "payment")
data class Payment(
    @Column(name = "amount", nullable = false)
    var amount: Double,

    @Column(name = "time", nullable = false)
    var time: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val buyer: Client,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "payment_id")
    var enrollment: Set<Enrollment>,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.NEW,

    @Column(name = "payu_id")
    var payuId: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
}

package eagle.dev.houseofculture.auth.model


import eagle.dev.houseofculture.user.model.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "password_change")
data class PasswordChange(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "account_id")
    val account: User? = null,

    val requestTime: Instant = Instant.now(),

    var code: String,

    @Enumerated(EnumType.STRING)
    var status: PasswordChangeStatus = PasswordChangeStatus.PENDING,

    val expirationTime: Instant = Instant.now().plusSeconds(60 * 5)
    )
package eagle.dev.houseofculture.user.model

import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@DiscriminatorValue("CHILD")
data class Child(
    override var id: Long? = null,
    override var password: String,
    override var status: UserStatus,
    override var email: String,
    override var phoneNumber: String? = null,
    override var firstName: String,
    override var lastName: String,
    override var birthdate: LocalDate,
    override var getsNewsletter: Boolean = false,
    @ManyToOne
    var parent: Client
) : User(id, password, UserRole.CHILD, status, email, phoneNumber, firstName, lastName, birthdate, getsNewsletter, "CHILD")
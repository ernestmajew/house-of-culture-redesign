package eagle.dev.houseofculture.user.model

import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@DiscriminatorValue("CLIENT")
data class Client(
    override var id: Long? = null,
    override var password: String,
    override var role: UserRole,
    override var status: UserStatus,
    override var email: String,
    override var phoneNumber: String? = null,
    override var firstName: String,
    override var lastName: String,
    override var birthdate: LocalDate,
    override var getsNewsletter: Boolean = false,
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    val children: List<Child> = listOf()
) : User(id, password, role, status, email, phoneNumber, firstName, lastName, birthdate, getsNewsletter, "CLIENT") {

}
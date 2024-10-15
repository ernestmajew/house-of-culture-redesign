package eagle.dev.houseofculture.user.model

import eagle.dev.houseofculture.enrollment.model.Enrollment
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.Proxy
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "user")
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Proxy(lazy = false)
abstract class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    open var id: Long? = null,

    @NotBlank
    @Access(AccessType.PROPERTY)
    open var password: String,

    @Enumerated(EnumType.STRING)
    @Access(AccessType.PROPERTY)
    open var role: UserRole,

    @Enumerated(EnumType.STRING)
    @Access(AccessType.PROPERTY)
    open var status: UserStatus,

    @Email
    @NotBlank
    @Access(AccessType.PROPERTY)
    open var email: String,

    @Access(AccessType.PROPERTY)
    open var phoneNumber: String? = null,

    @NotBlank
    @Access(AccessType.PROPERTY)
    open var firstName: String,

    @NotBlank
    @Access(AccessType.PROPERTY)
    open var lastName: String,

    @Access(AccessType.PROPERTY)
    open var birthdate: LocalDate,

    @Access(AccessType.PROPERTY)
    open var getsNewsletter: Boolean = false,

    @Column(name = "user_type", insertable = false, updatable = false)
    @Access(AccessType.PROPERTY)
    open var userType: String,
) {
    @OneToMany(mappedBy = "client")
    open var enrollments: Set<Enrollment> = emptySet()

    val userAgeInYears
        get() = ChronoUnit.YEARS.between(birthdate, LocalDate.now())
}

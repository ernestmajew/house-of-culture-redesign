package eagle.dev.houseofculture.contact.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "institution_contact_info")
data class ContactInfo(
    val institutionName: String,
    val addressFirstLine: String? = null,
    val addressSecondLine: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id var id: Int? = null
}
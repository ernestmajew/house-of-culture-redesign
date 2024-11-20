package eagle.dev.houseofculture.contact.repository

import eagle.dev.houseofculture.contact.model.ContactInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ContactInfoRepository: JpaRepository<ContactInfo, Int> {
    fun findFirstByOrderById(): Optional<ContactInfo>
}
package eagle.dev.houseofculture.contact.controller

import eagle.dev.houseofculture.contact.service.ContactInfoService
import eagle.dev.houseofculture.openapi.api.ContactInfoApi
import eagle.dev.houseofculture.openapi.model.ContactInfoTs
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional
class ContactInfoController(
    private val contactInfoService: ContactInfoService
) : ContactInfoApi {
    override fun getContactInfo(): ResponseEntity<ContactInfoTs> {
        return ResponseEntity.ok(contactInfoService.getContactInfo())
    }

    override fun updateContactInfo(contactInfoTs: ContactInfoTs): ResponseEntity<ContactInfoTs> {
        return ResponseEntity.ok(contactInfoService.updateContactInfo(contactInfoTs))
    }
}
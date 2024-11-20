package eagle.dev.houseofculture.contact.service.impl

import eagle.dev.houseofculture.contact.util.ContactInfoMapper
import eagle.dev.houseofculture.contact.model.ContactInfo
import eagle.dev.houseofculture.contact.repository.ContactInfoRepository
import eagle.dev.houseofculture.contact.service.ContactInfoService
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import eagle.dev.houseofculture.openapi.model.ContactInfoTs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ContactInfoServiceImpl(
    private val contactInfoRepository: ContactInfoRepository,
    private val mapper: ContactInfoMapper,
    @Value("\${app.institution.name}") private val institutionNameProperty: String
) : ContactInfoService {

    override fun getContactInfo(): ContactInfoTs {
        val contactInfo = contactInfoRepository.findFirstByOrderById()
            .orElseThrow { ObjectNotFoundException("Brak danych kontaktowych!") }

        return mapper.contactInfoToTs(contactInfo)
    }

    override fun updateContactInfo(updatedContactInfo: ContactInfoTs): ContactInfoTs {
        val oldContactInfo = contactInfoRepository.findFirstByOrderById().orElse(null)

        val newContactInfo = with(updatedContactInfo) {
            ContactInfo(institutionNameProperty, addressFirstLine, addressSecondLine, email, phoneNumber, facebookUrl, instagramUrl, latitude, longitude)
                .apply { id = oldContactInfo.id }
        }

        val savedContactInfo = contactInfoRepository.save(newContactInfo)
        return mapper.contactInfoToTs(savedContactInfo)
    }
}
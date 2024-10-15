package eagle.dev.houseofculture.configuration

import eagle.dev.houseofculture.contact.model.ContactInfo
import eagle.dev.houseofculture.contact.repository.ContactInfoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class InstitutionNameInit(
    private val contactInfoRepository: ContactInfoRepository,
    @Value("\${app.institution.name}") private val institutionName: String
): ApplicationListener<ContextRefreshedEvent?> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if(contactInfoRepository.count() == 0L) {
            contactInfoRepository.save(ContactInfo(institutionName))
        }
    }
}
package eagle.dev.houseofculture.contact.service

import eagle.dev.houseofculture.openapi.model.ContactInfoTs

interface ContactInfoService {
    fun getContactInfo(): ContactInfoTs
    fun updateContactInfo(updatedContactInfo: ContactInfoTs): ContactInfoTs
}
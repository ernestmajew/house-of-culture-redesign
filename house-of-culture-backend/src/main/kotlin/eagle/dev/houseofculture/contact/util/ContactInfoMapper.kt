package eagle.dev.houseofculture.contact.util

import eagle.dev.houseofculture.contact.model.ContactInfo
import eagle.dev.houseofculture.openapi.model.ContactInfoTs
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
@JvmDefaultWithCompatibility
interface ContactInfoMapper {
    fun contactInfoToTs(contactInfo: ContactInfo): ContactInfoTs
}

package eagle.dev.houseofculture.mail

import org.springframework.stereotype.Service
import java.util.*


@Service
class MailTemplatesService {
    //bundle baseName: package.filename
    private val bundleBaseName = "mail_templates.mail_templates"
    private val resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault())

    fun getTemplateByKey(key: String, vararg args: String) =
        getTemplateByKey(key).trim().format(*args)

    fun getTemplateByKey(key: String): String =
        runCatching {
            String(resourceBundle.getString(key).toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        }.getOrElse {
            throw NoSuchElementException("There is no template for key: $key")
        }
}
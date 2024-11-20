package eagle.dev.houseofculture.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@ConfigurationProperties(prefix = "mail-sender")
class MailSenderProperties(
    val host: String,
    val port: Int,
    val protocol: String,
)

@Configuration
class MailConfig(
    private val properties: MailSenderProperties
) {
    @Bean
    fun javaMailSender(): JavaMailSender =
        JavaMailSenderImpl().apply {
            host = properties.host
            port = properties.port
            protocol = properties.protocol
        }
}

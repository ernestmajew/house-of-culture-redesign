package eagle.dev.houseofculture.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class DevMailSender(
    private val mailSender: JavaMailSender,
    private val templatesService: MailTemplatesService,
    @Value("\${app.mail.default-address}") private val defaultSenderAddress: String
): MailSender {
    // throws NoSuchElement or non-existing templateKey

    @Async
    override fun sendMailWithTemplate(
        subject: String,
        targetEmail: String,
        templateKey: String,
        vararg templateVarargs: String
    ) =
        sendMailWithTemplate(subject, arrayOf(targetEmail), templateKey, *templateVarargs)

    @Async
    override fun sendMailWithTemplate(
        subject: String,
        targetEmail: Array<String>,
        templateKey: String,
        vararg templateVarargs: String
    ) =
        sendMail(subject, targetEmail, templatesService.getTemplateByKey(templateKey, *templateVarargs))
    @Async
    override fun sendMail(subject: String, targetEmail: String, text: String, vararg textVarargs: String) =
        sendMail(subject, arrayOf(targetEmail), text, *textVarargs)

    @Async
    override fun sendMail(subject: String, targetEmail: Array<String>, text: String, vararg textVarargs: String) {
        val formattedText = text.trim().format((textVarargs))

        val message = SimpleMailMessage().apply {
            this.subject = subject
            this.text = formattedText
            this.from = defaultSenderAddress
        }
        message.setTo(*targetEmail)

        mailSender.send(message)
    }
}
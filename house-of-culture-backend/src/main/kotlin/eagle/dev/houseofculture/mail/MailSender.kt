package eagle.dev.houseofculture.mail

interface MailSender {
    fun sendMailWithTemplate(
        subject: String,
        targetEmail: String,
        templateKey: String,
        vararg templateVarargs: String
    )

    fun sendMailWithTemplate(
        subject: String,
        targetEmail: Array<String>,
        templateKey: String,
        vararg templateVarargs: String
    )

    fun sendMail(subject: String, targetEmail: String, text: String, vararg textVarargs: String)

    fun sendMail(subject: String, targetEmail: Array<String>, text: String, vararg textVarargs: String)
}
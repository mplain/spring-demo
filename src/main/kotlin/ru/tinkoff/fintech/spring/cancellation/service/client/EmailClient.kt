package ru.tinkoff.fintech.spring.cancellation.service.client

import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import ru.tinkoff.fintech.spring.cancellation.model.EmailMessage

class EmailClient {

    private val restTemplate = RestTemplate()

    fun sendEmail(emailMessage: EmailMessage) {
        restTemplate.postForEntity<Void>(EMAIL_SERVICE_URL + SEND_EMAIL_PATH, emailMessage)
    }
}

private const val EMAIL_SERVICE_URL = "https://www.tinkoff.ru/email-server"
private const val SEND_EMAIL_PATH = "/send-email"

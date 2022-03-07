package ru.tinkoff.fintech.spring.cancellation.service.client

import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import ru.tinkoff.fintech.spring.cancellation.model.PaymentDetails
import ru.tinkoff.fintech.spring.cancellation.model.ProviderDetails

class PaymentClient {

    private val restTemplate = RestTemplate()

    fun getPaymentDetails(paymentId: Long): PaymentDetails =
        restTemplate.getForObject(PAYMENT_SERVICE_URL + GET_PAYMENT_DETAILS_PATH, paymentId)

    fun getProviderDetails(providerId: Int): ProviderDetails =
        restTemplate.getForObject(PAYMENT_SERVICE_URL + GET_PROVIDER_DETAILS_PATH, providerId)
}

private const val PAYMENT_SERVICE_URL = "https://www.tinkoff.ru/payment-gate"
private const val GET_PAYMENT_DETAILS_PATH = "/payment/{id}"
private const val GET_PROVIDER_DETAILS_PATH = "/provider/{id}"

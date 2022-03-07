package ru.tinkoff.fintech.spring.cancellation.service

import ru.tinkoff.fintech.spring.cancellation.model.CancelPaymentRequest
import ru.tinkoff.fintech.spring.cancellation.model.EmailMessage
import ru.tinkoff.fintech.spring.cancellation.model.PaymentDetails
import ru.tinkoff.fintech.spring.cancellation.model.ProviderDetails
import ru.tinkoff.fintech.spring.cancellation.service.client.EmailClient
import ru.tinkoff.fintech.spring.cancellation.service.client.PaymentClient

class CancelPaymentService {

    private val paymentClient = PaymentClient()
    private val emailClient = EmailClient()

    fun cancelPayment(request: CancelPaymentRequest) {
        val payment = paymentClient.getPaymentDetails(request.paymentId)
        val provider = paymentClient.getProviderDetails(payment.providerId)
        sendCancelPaymentRequest(payment, provider)
    }

    private fun sendCancelPaymentRequest(payment: PaymentDetails, provider: ProviderDetails) {
        val emailMessage = EmailMessage(
            to = provider.email,
            subject = "Отмена платежа ${provider.name}",
            body = "Просьба отменить платеж:\n$payment"
        )
        return emailClient.sendEmail(emailMessage)
    }
}

package ru.tinkoff.fintech.spring.cancellation.model

data class CancelPaymentRequest(
    val paymentId: Long,
    val comment: String? = null
)

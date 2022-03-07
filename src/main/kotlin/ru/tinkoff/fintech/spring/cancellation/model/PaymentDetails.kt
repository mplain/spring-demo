package ru.tinkoff.fintech.spring.cancellation.model

import java.math.BigDecimal

data class PaymentDetails(
    val id: Long,
    val amount: BigDecimal,
    val currency: String,
    val datetime: String,
    val payerInfo: String,
    val payeeInfo: String,
    val providerId: Int
)

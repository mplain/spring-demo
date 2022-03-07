package ru.tinkoff.fintech.spring.cancellation.model

data class EmailMessage(
    val to: String,
    val cc: String? = null,
    val from: String? = null,
    val subject: String,
    val body: String
)

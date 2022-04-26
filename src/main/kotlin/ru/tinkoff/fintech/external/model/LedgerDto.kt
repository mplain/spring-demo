package ru.tinkoff.fintech.external.model

data class SaveOrderRequest(val price: Double)

data class SaveOrderResponse(val orderId: Int)

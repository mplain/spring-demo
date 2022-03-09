package ru.tinkoff.fintech.pizza.model.external

data class SaveOrderRequest(val price: Double)

data class SaveOrderResponse(val orderId: Int)

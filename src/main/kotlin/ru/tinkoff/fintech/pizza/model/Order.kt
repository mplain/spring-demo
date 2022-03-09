package ru.tinkoff.fintech.pizza.model

data class Order(
    val id: Int,
    val food: Food,
    val status: Status = Status.IN_PROGRESS
)

data class OrderResponse<T>(
    val item: T? = null,
    val status: Status,
    val change: Double = 0.0,
    val comment: String? = null
)

enum class Status { IN_PROGRESS, READY, DECLINED }

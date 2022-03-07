package ru.tinkoff.fintech.pizza.model

data class Order(
    val id: Int,
    val food: Food,
    val status: Status = Status.IN_PROGRESS
)

enum class Status { IN_PROGRESS, READY }

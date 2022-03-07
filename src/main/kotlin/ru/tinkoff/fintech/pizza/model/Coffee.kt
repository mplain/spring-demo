package ru.tinkoff.fintech.pizza.model

data class Coffee(
    override val name: String,
    val brewTime: Int
) : Food

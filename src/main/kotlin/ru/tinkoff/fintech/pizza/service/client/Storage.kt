package ru.tinkoff.fintech.pizza.service.client

interface Storage {

    fun getAmount(ingredient: String): Int

    fun take(ingredient: String, amount: Int)
}

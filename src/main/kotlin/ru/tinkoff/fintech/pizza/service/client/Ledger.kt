package ru.tinkoff.fintech.pizza.service.client

import ru.tinkoff.fintech.pizza.model.Coffee

interface Ledger {

    fun getIngredientPrice(item: String): Double

    fun getCoffeePrice(coffee: Coffee): Double

    fun saveOrder(price: Double): Int
}

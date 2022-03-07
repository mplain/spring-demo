package ru.tinkoff.fintech.pizza.service.client

import ru.tinkoff.fintech.pizza.model.Coffee

class Ledger {

    private val ingredientPrices: Map<String, Double> = mapOf(
        "яйца" to 3.48,
        "бекон" to 6.48,
        "тесто" to 1.00,
        "томат" to 1.53,
        "оливки" to 1.53,
        "сыр" to 0.98,
        "пармезан" to 3.98,
        "грибы" to 3.34,
        "спаржа" to 3.34,
        "мясное ассорти" to 9.38,
        "вяленая говядина" to 12.24
    )

    private val coffeePrices: Map<String, Double> = mapOf(
        "эспрессо" to 5.0,
        "капучино" to 3.48
    )

    private var orderCounter = 0
    private var cash = 0.0

    fun getIngredientPrice(item: String): Double = ingredientPrices[item] ?: error("Неизвестный ингредиент")

    fun getCoffeePrice(coffee: Coffee): Double = coffeePrices[coffee.name] ?: error("Неизвестный вид кофе")

    fun saveOrder(price: Double): Int {
        val orderId = ++orderCounter
        cash += price
        println("Order #$orderId saved to ledger, price: $price")
        return orderId
    }
}

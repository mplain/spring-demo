package ru.tinkoff.fintech.pizza.model

data class Pizza(
    override val name: String,
    val ingredients: Map<String, Int>
) : Food

data class PizzaMenuItem(
    val name: String,
    val ingredients: Map<String, Int>,
    val price: Double
) {
    constructor(pizza: Pizza, price: Double) : this(pizza.name, pizza.ingredients, price)
}

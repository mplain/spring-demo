package ru.tinkoff.fintech.pizza.model

data class Coffee(
    override val name: String,
    val brewTime: Int
) : Food

data class CoffeeMenuItem(
    val name: String,
    val brewTime: Int,
    val price: Double
) {
    constructor(coffee: Coffee, price: Double) : this(coffee.name, coffee.brewTime, price)
}

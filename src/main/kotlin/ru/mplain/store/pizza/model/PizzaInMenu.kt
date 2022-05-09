package ru.mplain.store.pizza.model

import java.math.BigDecimal

data class PizzaInMenu(
    val name: String,
    val ingredients: Set<String>,
    val price: BigDecimal
) {

    constructor(pizza: Pizza, price: BigDecimal) : this(pizza.name, pizza.ingredients.keys, price)
}

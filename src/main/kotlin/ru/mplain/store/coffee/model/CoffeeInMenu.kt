package ru.mplain.store.coffee.model

import java.math.BigDecimal

data class CoffeeInMenu(
    val name: String,
    val brewTime: Int,
    val price: BigDecimal
) {

    constructor(coffee: Coffee, price: BigDecimal) : this(coffee.name, coffee.brewTime, price)
}

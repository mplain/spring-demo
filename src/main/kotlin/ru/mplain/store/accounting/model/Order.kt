package ru.mplain.store.accounting.model

import ru.mplain.store.coffee.model.Coffee
import ru.mplain.store.pizza.model.Pizza
import java.math.BigDecimal

data class Order(
    val id: Long = 0,
    val type: FoodType,
    val name: String,
    val cash: BigDecimal,
    val price: BigDecimal,
    val status: Status = Status.IN_PROGRESS,
    val comment: String? = null
) {

    constructor(coffee: Coffee, cash: BigDecimal, price: BigDecimal) :
        this(0, FoodType.COFFEE, coffee.name, cash, price)

    constructor(pizza: Pizza, cash: BigDecimal, price: BigDecimal) :
        this(0, FoodType.PIZZA, pizza.name, cash, price)

    fun decline(comment: String?) = copy(
        status = Status.DECLINED,
        comment = comment ?: "Unknown error"
    )
}

enum class FoodType { COFFEE, PIZZA }
enum class Status { IN_PROGRESS, READY, DECLINED }

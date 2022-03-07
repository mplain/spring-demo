package ru.tinkoff.fintech.pizza.service.client

import ru.tinkoff.fintech.pizza.model.Coffee

class BarMenu {

    private val coffeeMenu = setOf(
        Coffee("эспрессо", 5),
        Coffee("капучино", 6)
    )

    fun getCoffeeMenu(): Set<Coffee> = coffeeMenu

    fun getCoffee(name: String): Coffee? = coffeeMenu.find { it.name == name }
}

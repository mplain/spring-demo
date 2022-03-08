package ru.tinkoff.fintech.pizza.service

import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.Order
import ru.tinkoff.fintech.pizza.service.client.BarMenu

class Bar(private val barMenu: BarMenu) {

    fun getCoffeeMenu(): Set<Coffee> = barMenu.getCoffeeMenu()

    fun getCoffee(name: String): Coffee = barMenu.getCoffee(name) ?: error("Нет такого кофе в меню!")

    fun order(order: Order): Coffee {
        val coffee = order.food
        require(coffee is Coffee) { "Мы готовим только кофе!" }
        return brew(coffee)
    }

    private fun brew(coffee: Coffee): Coffee {
        val time = coffee.brewTime * 100L
        Thread.sleep(time)
        return coffee
    }
}

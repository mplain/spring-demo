package ru.tinkoff.fintech.pizza.service.client

import ru.tinkoff.fintech.pizza.model.Coffee

interface BarMenu {

    fun getCoffeeMenu(): Set<Coffee>

    fun getCoffee(name: String): Coffee?
}

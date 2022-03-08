package ru.tinkoff.fintech.pizza.service.client

import ru.tinkoff.fintech.pizza.model.Pizza

interface PizzaMenu {

    fun getPizzaMenu(): Set<Pizza>

    fun getPizza(name: String): Pizza?
}

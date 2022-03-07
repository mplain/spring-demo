package ru.tinkoff.fintech.pizza.service.client

import ru.tinkoff.fintech.pizza.model.Pizza

class PizzaMenu {

    private val pizzaMenu = setOf(
        Pizza("карбонара", mapOf("яйца" to 1, "бекон" to 2, "тесто" to 1, "сыр" to 2)),
        Pizza("маринара", mapOf("томат" to 2, "оливки" to 3, "тесто" to 1)),
        Pizza("сардиния", mapOf("салями" to 3, "оливки" to 1, "тесто" to 1, "сыр" to 3)),
        Pizza("вальтеллина", mapOf("вяленая говядина" to 1, "зелень" to 1, "тесто" to 1, "пармезан" to 2)),
        Pizza("крестьянская", mapOf("грибы" to 3, "томат" to 1, "тесто" to 1, "спаржа" to 1, "мясное ассорти" to 1))
    )

    fun getPizzaMenu(): Set<Pizza> = pizzaMenu

    fun getPizza(name: String): Pizza? = pizzaMenu.find { it.name == name }
}

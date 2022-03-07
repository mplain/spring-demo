package ru.tinkoff.fintech.pizza

import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.Food
import ru.tinkoff.fintech.pizza.model.Pizza
import ru.tinkoff.fintech.pizza.service.Accounting
import ru.tinkoff.fintech.pizza.service.Bar
import ru.tinkoff.fintech.pizza.service.Kitchen

class PizzaStore {

    private val bar = Bar()
    private val kitchen = Kitchen()
    private val accounting = Accounting()

    fun getCoffeeMenu(): Map<Coffee, Double> =
        bar.getCoffeeMenu().associateWith(accounting::getCoffeePrice)

    fun orderCoffee(name: String, cash: Double): Pair<Coffee?, Double> =
        try {
            val coffee = bar.getCoffee(name)
            val (order, change) = accounting.orderCoffee(coffee, cash)
            val result = bar.order(order)
            result to change
        } catch (e: Exception) {
            println(e.message)
            null to cash
        }

    fun getPizzaMenu(): Map<Pizza, Double> =
        kitchen.getPizzaMenu().associateWith(accounting::getPizzaPrice)

    fun orderPizza(name: String, money: Double): Pair<Int?, Double> =
        try {
            val pizza = kitchen.getPizza(name)
            val (order, change) = accounting.orderPizza(pizza, money)
            kitchen.order(order)
            order.id to change
        } catch (e: Exception) {
            println(e.message)
            null to money
        }

    fun getPizzaIfReady(orderId: Int): Food? =
        if (kitchen.isOrderReady(orderId)) kitchen.getOrder(orderId) else null
}

package ru.tinkoff.fintech.pizza.service

import ru.tinkoff.fintech.pizza.model.Food
import ru.tinkoff.fintech.pizza.model.Order
import ru.tinkoff.fintech.pizza.model.Pizza
import ru.tinkoff.fintech.pizza.model.Status
import ru.tinkoff.fintech.pizza.service.client.PizzaMenu
import ru.tinkoff.fintech.pizza.service.client.Storage
import java.util.concurrent.ForkJoinPool

class Kitchen(
    private val pizzaMenu: PizzaMenu,
    private val storage: Storage
) {

    private val workers = ForkJoinPool(2)
    private val orders = mutableMapOf<Int, Order>()

    fun getPizzaMenu(): Set<Pizza> = pizzaMenu.getPizzaMenu()

    fun getPizza(name: String): Pizza = pizzaMenu.getPizza(name) ?: error("Нет такой пиццы в меню!")

    fun order(order: Order) {
        val pizza = order.food
        require(pizza is Pizza) { "Мы готовим только пиццу!" }
        takeIngredients(pizza.ingredients)
        workers.execute {
            orders[order.id] = order
            println("Заказ ${order.id} принят: ${pizza.name}")
            cook(pizza)
            orders[order.id] = order.copy(status = Status.READY)
            println("Заказ ${order.id} готов: ${pizza.name}")
        }
    }

    private fun cook(pizza: Pizza) {
        val time = pizza.ingredients.values.sum() * 100L
        Thread.sleep(time)
    }

    fun isOrderReady(orderId: Int): Boolean = orders[orderId]?.status == Status.READY

    fun getOrder(orderId: Int): Food = orders.remove(orderId)?.food ?: error("Нет такого заказа!")

    private fun takeIngredients(ingredients: Map<String, Int>) {
        check(hasEnough(ingredients)) { "Недостаточно ингредиентов!" }
        ingredients.forEach { (ingredient, amount) -> storage.take(ingredient, amount) }
    }

    private fun hasEnough(ingredients: Map<String, Int>): Boolean =
        ingredients.all { (ingredient, amount) -> storage.getAmount(ingredient) >= amount }
}

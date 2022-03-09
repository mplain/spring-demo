package ru.tinkoff.fintech.pizza.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.pizza.model.Order
import ru.tinkoff.fintech.pizza.model.Pizza
import ru.tinkoff.fintech.pizza.model.Status
import ru.tinkoff.fintech.pizza.service.client.PizzaMenuClient
import ru.tinkoff.fintech.pizza.service.client.StorageClient
import java.util.concurrent.ConcurrentHashMap

@Service
class KitchenService(
    private val pizzaMenuClient: PizzaMenuClient,
    private val storageClient: StorageClient
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val orders = ConcurrentHashMap<Int, Order>()

    fun getPizzaMenu(): Set<Pizza> = pizzaMenuClient.getPizzaMenu()

    fun getPizza(name: String): Pizza {
        val pizza = pizzaMenuClient.getPizza(name)
        return requireNotNull(pizza) { "Нет такой пиццы в меню!" }
    }

    fun order(order: Order) {
        val pizza = order.food
        require(pizza is Pizza) { "Мы готовим только пиццу!" }
        takeIngredients(pizza.ingredients)
        CoroutineScope(Dispatchers.Default).launch {
            orders[order.id] = order
            log.info("Заказ ${order.id} принят: ${pizza.name}")
            cook(pizza)
            orders[order.id] = order.copy(status = Status.READY)
            log.info("Заказ ${order.id} готов: ${pizza.name}")
        }
    }

    private fun cook(pizza: Pizza) {
        val time = pizza.ingredients.values.sum() * 100L
        Thread.sleep(time)
    }

    fun isOrderReady(orderId: Int): Boolean {
        val order = orders[orderId]
        requireNotNull(order) { "Нет такого заказа!" }
        return order.status == Status.READY
    }

    fun getOrder(orderId: Int): Pizza {
        val order = orders.remove(orderId)
        requireNotNull(order) { "Нет такого заказа!" }
        return order.food as Pizza
    }

    private fun takeIngredients(ingredients: Map<String, Int>) {
        check(hasEnough(ingredients)) { "Недостаточно ингредиентов!" }
        ingredients.forEach { (ingredient, amount) -> storageClient.take(ingredient, amount) }
    }

    private fun hasEnough(ingredients: Map<String, Int>): Boolean =
        ingredients.all { (ingredient, amount) -> storageClient.getAmount(ingredient) >= amount }
}

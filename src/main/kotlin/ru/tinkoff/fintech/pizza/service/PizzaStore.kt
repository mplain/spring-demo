package ru.tinkoff.fintech.pizza.service

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.pizza.model.*

@Service
class PizzaStore(
    private val barService: BarService,
    private val kitchenService: KitchenService,
    private val accounting: Accounting
) {

    fun getCoffeeMenu(): Set<CoffeeMenuItem> =
        barService.getCoffeeMenu().map { coffee ->
            val price = accounting.getCoffeePrice(coffee)
            CoffeeMenuItem(coffee, price)
        }.toSet()

    fun orderCoffee(name: String, cash: Double): OrderResponse<Coffee> =
        try {
            val coffee = barService.getCoffee(name)
            val (order, change) = accounting.orderCoffee(coffee, cash)
            val result = barService.order(order)
            OrderResponse(result, Status.READY, change)
        } catch (e: Exception) {
            OrderResponse(null, Status.DECLINED, cash, e.message)
        }

    fun getPizzaMenu(): Set<PizzaMenuItem> =
        kitchenService.getPizzaMenu().map { pizza ->
            val price = accounting.getPizzaPrice(pizza)
            PizzaMenuItem(pizza, price)
        }.toSet()

    fun orderPizza(name: String, cash: Double): OrderResponse<Int> =
        try {
            val pizza = kitchenService.getPizza(name)
            val (order, change) = accounting.orderPizza(pizza, cash)
            kitchenService.order(order)
            OrderResponse(order.id, Status.IN_PROGRESS, change)
        } catch (e: Exception) {
            OrderResponse(null, Status.DECLINED, cash, e.message)
        }

    fun getPizzaIfReady(orderId: Int): OrderResponse<Pizza> {
        val isOrderReady = kitchenService.isOrderReady(orderId)
        return if (isOrderReady) {
            val pizza = kitchenService.getOrder(orderId)
            OrderResponse(pizza, Status.READY)
        } else {
            OrderResponse(item = null, Status.IN_PROGRESS)
        }
    }
}

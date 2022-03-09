package ru.tinkoff.fintech.pizza

import org.springframework.web.bind.annotation.*
import ru.tinkoff.fintech.pizza.model.OrderResponse
import ru.tinkoff.fintech.pizza.model.Pizza
import ru.tinkoff.fintech.pizza.model.PizzaMenuItem
import ru.tinkoff.fintech.pizza.service.PizzaStore

@RestController
@RequestMapping("/pizza")
class PizzaController(private val pizzaStore: PizzaStore) {

    @GetMapping("/menu")
    fun getPizzaMenu(): Set<PizzaMenuItem> =
        pizzaStore.getPizzaMenu()

    @PostMapping("/order")
    fun orderPizza(@RequestParam name: String, @RequestParam cash: Double): OrderResponse<Int> =
        pizzaStore.orderPizza(name, cash)

    @GetMapping("/order/{orderId}")
    fun getPizzaIfReady(@PathVariable orderId: Int): OrderResponse<Pizza> =
        pizzaStore.getPizzaIfReady(orderId)
}

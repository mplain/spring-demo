package ru.tinkoff.fintech.pizza

import org.springframework.web.bind.annotation.*
import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.CoffeeMenuItem
import ru.tinkoff.fintech.pizza.model.OrderResponse
import ru.tinkoff.fintech.pizza.service.PizzaStore

@RestController
@RequestMapping("/coffee")
class CoffeeController(private val pizzaStore: PizzaStore) {

    @GetMapping("/menu")
    fun getCoffeeMenu(): Set<CoffeeMenuItem> =
        pizzaStore.getCoffeeMenu()

    @PostMapping("/order")
    fun orderCoffee(@RequestParam name: String, @RequestParam cash: Double): OrderResponse<Coffee> =
        pizzaStore.orderCoffee(name, cash)
}

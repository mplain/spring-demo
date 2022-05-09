package ru.mplain.store.pizza

import org.springframework.web.bind.annotation.*
import ru.mplain.store.accounting.model.Order
import ru.mplain.store.pizza.model.PizzaInMenu
import ru.mplain.store.pizza.service.PizzaService
import java.math.BigDecimal

@RestController
@RequestMapping("/pizza")
class PizzaController(private val pizzaService: PizzaService) {

    @GetMapping("/menu")
    fun getPizzaMenu(): List<PizzaInMenu> =
        pizzaService.getPizzaMenu()

    @PostMapping("/order")
    fun orderPizza(@RequestParam name: String, @RequestParam cash: BigDecimal): Order =
        pizzaService.orderPizza(name, cash)

    @GetMapping("/order/{orderId}")
    fun getOrder(@PathVariable orderId: Long): Order =
        pizzaService.getOrder(orderId)
}

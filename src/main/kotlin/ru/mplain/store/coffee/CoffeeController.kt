package ru.mplain.store.coffee

import org.springframework.web.bind.annotation.*
import ru.mplain.store.accounting.model.Order
import ru.mplain.store.coffee.model.CoffeeInMenu
import ru.mplain.store.coffee.service.CoffeeService
import java.math.BigDecimal

@RestController
@RequestMapping("/coffee")
class CoffeeController(private val coffeeService: CoffeeService) {

    @GetMapping("/menu")
    fun getCoffeeMenu(): List<CoffeeInMenu> =
        coffeeService.getCoffeeMenu()

    @PostMapping("/order")
    fun orderCoffee(@RequestParam name: String, @RequestParam cash: BigDecimal): Order =
        coffeeService.orderCoffee(name, cash)
}

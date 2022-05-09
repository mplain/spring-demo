package ru.mplain.store.coffee.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.mplain.store.accounting.model.FoodType
import ru.mplain.store.accounting.model.Order
import ru.mplain.store.accounting.model.Status
import ru.mplain.store.accounting.service.dao.OrderDao
import ru.mplain.store.coffee.model.Coffee
import ru.mplain.store.coffee.model.CoffeeInMenu
import java.math.BigDecimal

@Service
class CoffeeService(
    private val coffeeIntegrationService: CoffeeIntegrationService,
    private val orderDao: OrderDao,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getCoffeeMenu(): List<CoffeeInMenu> =
        coffeeIntegrationService.getCoffeeMenu()
            .map { (coffee, price) -> CoffeeInMenu(coffee, price) }

    fun orderCoffee(name: String, cash: BigDecimal): Order =
        try {
            val (coffee, price) = getCoffee(name) ?: error("Coffee $name not available")
            require(cash >= price) { "Not enough money, price is $price" }
            val order = saveOrder(coffee, cash, price)
            brew(coffee)
            order.copy(status = Status.READY)
                .also(orderDao::updateOrder)
        } catch (e: Exception) {
            log.error("Error ordering coffee", e)
            Order(0, FoodType.COFFEE, name, cash, BigDecimal.ZERO).decline(e.message)
        }

    private fun getCoffee(name: String): Map.Entry<Coffee, BigDecimal>? =
        coffeeIntegrationService.getCoffeeMenu().entries.find { it.key.name == name }

    private fun saveOrder(coffee: Coffee, cash: BigDecimal, price: BigDecimal): Order {
        val order = Order(coffee, cash, price)
        val orderId = orderDao.saveOrder(order)
        return order.copy(id = orderId)
    }

    private fun brew(coffee: Coffee) {
        val time = coffee.brewTime * 100L
        Thread.sleep(time)
    }
}

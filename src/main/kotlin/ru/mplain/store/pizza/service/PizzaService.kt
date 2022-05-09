package ru.mplain.store.pizza.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.mplain.store.accounting.model.FoodType
import ru.mplain.store.accounting.model.Order
import ru.mplain.store.accounting.model.Status
import ru.mplain.store.accounting.service.dao.OrderDao
import ru.mplain.store.pizza.model.Pizza
import ru.mplain.store.pizza.model.PizzaInMenu
import java.math.BigDecimal

@Service
class PizzaService(
    private val pizzaIntegrationService: PizzaIntegrationService,
    private val orderDao: OrderDao
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getPizzaMenu(): List<PizzaInMenu> =
        pizzaIntegrationService.getPizzaMenu()
            .map { (pizza, price) -> PizzaInMenu(pizza, price) }

    fun orderPizza(name: String, cash: BigDecimal): Order =
        try {
            val (pizza, price) = pizzaIntegrationService.getPizza(name) ?: error("Pizza $name not available")
            require(cash >= price) { "Not enough money, price is $price" }
            val order = saveOrder(pizza, cash, price)
            CoroutineScope(Dispatchers.Default).launch { processOrder(order, pizza) }
            order
        } catch (e: Exception) {
            log.error("Error ordering pizza", e)
            Order(0, FoodType.PIZZA, name, cash, BigDecimal.ZERO).decline(e.message)
        }

    fun getOrder(orderId: Long): Order {
        val order = orderDao.getOrder(orderId)
        requireNotNull(order) { "Order $orderId not registered" }
        return order
    }

    private fun saveOrder(pizza: Pizza, cash: BigDecimal, price: BigDecimal): Order {
        val order = Order(pizza, cash, price)
        val orderId = orderDao.saveOrder(order)
        return order.copy(id = orderId)
    }

    private suspend fun processOrder(order: Order, pizza: Pizza) {
        log.info("Order ${order.id} for pizza ${pizza.name} in progress")
        runCatching {
            pizzaIntegrationService.takeIngredients(pizza)
            cook(pizza)
        }.onSuccess {
            orderDao.updateOrder(order.copy(status = Status.READY))
            log.info("Order ${order.id} for pizza ${pizza.name} is ready")
        }.onFailure {
            if (it is IllegalStateException) {
                pizzaIntegrationService.insufficientIngredients(pizza.name)
            }
            orderDao.updateOrder(order.decline(comment = it.message))
            log.error("Error processing order ${order.id} for pizza ${pizza.name}", it)
        }
    }

    private suspend fun cook(pizza: Pizza) {
        val time = pizza.ingredients.values.sum() * 100L
        delay(time)
    }
}

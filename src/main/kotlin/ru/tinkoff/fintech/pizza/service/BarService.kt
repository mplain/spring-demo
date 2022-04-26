package ru.tinkoff.fintech.pizza.service

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.Order
import ru.tinkoff.fintech.pizza.service.client.MenuDao

@Service
class BarService(private val menuDao: MenuDao) {

    fun getCoffeeMenu(): Set<Coffee> = menuDao.getCoffeeMenu()

    fun getCoffee(name: String): Coffee {
        val coffee = menuDao.getCoffee(name)
        return requireNotNull(coffee) { "Нет такого кофе в меню!" }
    }

    fun order(order: Order): Coffee {
        val coffee = order.food
        require(coffee is Coffee) { "Мы готовим только кофе!" }
        return brew(coffee)
    }

    private fun brew(coffee: Coffee): Coffee {
        val time = coffee.brewTime * 100L
        Thread.sleep(time)
        return coffee
    }
}

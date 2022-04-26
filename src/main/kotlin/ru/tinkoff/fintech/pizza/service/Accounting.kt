package ru.tinkoff.fintech.pizza.service

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.Food
import ru.tinkoff.fintech.pizza.model.Order
import ru.tinkoff.fintech.pizza.model.Pizza
import ru.tinkoff.fintech.pizza.service.client.LedgerClient

@Service
class Accounting(private val ledgerClient: LedgerClient) {

    fun getCoffeePrice(coffee: Coffee): Double = ledgerClient.getCoffeePrice(coffee)

    fun getPizzaPrice(pizza: Pizza): Double =
        pizza.ingredients.map { (ingredient, amount) -> ledgerClient.getIngredientPrice(ingredient) * amount }.sum()

    fun orderCoffee(coffee: Coffee, cash: Double): Pair<Order, Double> {
        val price = getCoffeePrice(coffee)
        return order(coffee, price, cash)
    }

    fun orderPizza(pizza: Pizza, cash: Double): Pair<Order, Double> {
        val price = getPizzaPrice(pizza)
        return order(pizza, price, cash)
    }

    private fun order(food: Food, price: Double, money: Double): Pair<Order, Double> {
        require(money >= price) { "Недостаточно денег!" }
        val orderId = ledgerClient.saveOrder(price)
        val order = Order(orderId, food)
        val change = money - price
        return order to change
    }
}

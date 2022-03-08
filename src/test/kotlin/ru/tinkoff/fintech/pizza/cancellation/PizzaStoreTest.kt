package ru.tinkoff.fintech.pizza.cancellation

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import ru.tinkoff.fintech.pizza.PizzaStore

class PizzaStoreTest : FeatureSpec() {

    private val pizzaStore = PizzaStore()

    init {
        feature("order coffee") {
            scenario("success") {
                val coffeeMenu = pizzaStore.getCoffeeMenu()
                val coffee = coffeeMenu.keys.first()
                val price = coffeeMenu.getValue(coffee)
                val cash = price + 1

                val (result, change) = pizzaStore.orderCoffee(coffee.name, cash)

                result shouldBe coffee
                change shouldBe (cash - price)
            }
            scenario("failure - unknown coffee") {
                val coffeeName = "лавандовый раф"
                val cash = Double.MAX_VALUE
                val (result, change) = pizzaStore.orderCoffee(coffeeName, cash)

                result shouldBe null
                change shouldBe cash
            }
            scenario("failure - insufficient cash") {
                val coffeeMenu = pizzaStore.getCoffeeMenu()
                val coffee = coffeeMenu.keys.first()
                val price = coffeeMenu.getValue(coffee)
                val cash = price - 1

                val (result, change) = pizzaStore.orderCoffee(coffee.name, cash)

                result shouldBe null
                change shouldBe cash
            }
        }
        feature("order pizza") {
            scenario("success") {
                val pizzaMenu = pizzaStore.getPizzaMenu()
                val pizza = pizzaMenu.keys.first()
                val price = pizzaMenu.getValue(pizza)
                val cash = price + 1

                val (orderId, change) = pizzaStore.orderPizza(pizza.name, cash)

                orderId.shouldNotBeNull()
                change shouldBe (cash - price)

                pizzaStore.getPizzaIfReady(orderId).shouldBeNull()
                delay(1000)
                pizzaStore.getPizzaIfReady(orderId) shouldBe pizza
            }
            scenario("failure - unknown pizza") {
                val pizzaName = "диабло 2"
                val cash = Double.MAX_VALUE
                val (result, change) = pizzaStore.orderPizza(pizzaName, cash)

                result shouldBe null
                change shouldBe cash
            }
            scenario("failure - insufficient cash") {
                val pizzaMenu = pizzaStore.getPizzaMenu()
                val pizza = pizzaMenu.keys.first()
                val price = pizzaMenu.getValue(pizza)
                val cash = price - 1

                val (result, change) = pizzaStore.orderPizza(pizza.name, cash)

                result shouldBe null
                change shouldBe cash
            }
        }
    }
}

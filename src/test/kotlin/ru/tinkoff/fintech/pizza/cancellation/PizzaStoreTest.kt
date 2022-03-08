package ru.tinkoff.fintech.pizza.cancellation

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import ru.tinkoff.fintech.pizza.PizzaStore
import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.Pizza
import ru.tinkoff.fintech.pizza.service.Accounting
import ru.tinkoff.fintech.pizza.service.Bar
import ru.tinkoff.fintech.pizza.service.Kitchen
import ru.tinkoff.fintech.pizza.service.client.BarMenu
import ru.tinkoff.fintech.pizza.service.client.Ledger
import ru.tinkoff.fintech.pizza.service.client.PizzaMenu
import ru.tinkoff.fintech.pizza.service.client.Storage

class PizzaStoreTest : FeatureSpec() {

    private val barMenu = mockk<BarMenu>()
    private val pizzaMenu = mockk<PizzaMenu>()
    private val storage = mockk<Storage>()
    private val ledger = mockk<Ledger>()
    private val pizzaStore = PizzaStore(Bar(barMenu), Kitchen(pizzaMenu, storage), Accounting(ledger))
    private var orderCounter = 0
    private var cash = 0.0

    override fun beforeEach(testCase: TestCase) {
        every { barMenu.getCoffeeMenu() } returns coffeeTypes
        every { barMenu.getCoffee(any()) } answers { coffeeTypes.find { it.name == firstArg() } }
        every { pizzaMenu.getPizzaMenu() } returns pizzaTypes
        every { pizzaMenu.getPizza(any()) } answers { pizzaTypes.find { it.name == firstArg() } }
        every { storage.getAmount(any()) } answers { storageAmounts[firstArg()] ?: 0 }
        every { storage.take(any(), any()) } answers {
            storageAmounts[firstArg()] = storageAmounts.getValue(firstArg()) - arg<Int>(1)
        }
        every { ledger.getIngredientPrice(any()) } answers { ingredientPrices.getValue(firstArg()) }
        every { ledger.getCoffeePrice(any()) } answers { coffeePrices.getValue(firstArg<Coffee>().name) }
        every { ledger.saveOrder(any()) } answers { cash += firstArg<Double>(); ++orderCounter }
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
        orderCounter = 0
        cash = 0.0
    }

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

    private val coffeeTypes = setOf(
        Coffee("эспрессо", 5),
        Coffee("капучино", 6)
    )

    private val pizzaTypes = setOf(
        Pizza("карбонара", mapOf("яйца" to 1, "бекон" to 2, "тесто" to 1, "сыр" to 2)),
        Pizza("маринара", mapOf("томат" to 2, "оливки" to 3, "тесто" to 1)),
        Pizza("сардиния", mapOf("салями" to 3, "оливки" to 1, "тесто" to 1, "сыр" to 3)),
        Pizza("вальтеллина", mapOf("вяленая говядина" to 1, "зелень" to 1, "тесто" to 1, "пармезан" to 2)),
        Pizza("крестьянская", mapOf("грибы" to 3, "томат" to 1, "тесто" to 1, "спаржа" to 1, "мясное ассорти" to 1))
    )

    private val storageAmounts = mutableMapOf(
        "яйца" to 20,
        "бекон" to 20,
        "тесто" to 20,
        "томат" to 20,
        "оливки" to 20,
        "сыр" to 20,
        "пармезан" to 20,
        "грибы" to 20,
        "спаржа" to 20,
        "мясное ассорти" to 20,
        "вяленая говядина" to 20,
        "салями" to 20,
        "зелень" to 20
    )

    private val ingredientPrices = mapOf(
        "яйца" to 3.48,
        "бекон" to 6.48,
        "тесто" to 1.00,
        "томат" to 1.53,
        "оливки" to 1.53,
        "сыр" to 0.98,
        "пармезан" to 3.98,
        "грибы" to 3.34,
        "спаржа" to 3.34,
        "мясное ассорти" to 9.38,
        "вяленая говядина" to 12.24,
        "салями" to 8.0,
        "зелень" to 2.0
    )

    private val coffeePrices = mapOf(
        "эспрессо" to 5.0,
        "капучино" to 3.48
    )
}

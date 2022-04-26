package ru.tinkoff.fintech.pizza.cancellation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import kotlinx.coroutines.delay
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import ru.tinkoff.fintech.pizza.model.*
import ru.tinkoff.fintech.pizza.service.client.MenuDao
import ru.tinkoff.fintech.pizza.service.client.LedgerClient
import ru.tinkoff.fintech.pizza.service.client.PizzaMenuDao
import ru.tinkoff.fintech.pizza.service.client.StorageClient
import kotlin.random.Random.Default.nextInt
import kotlin.text.Charsets.UTF_8

@SpringBootTest
@AutoConfigureMockMvc
class PizzaStoreTest(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper) : FeatureSpec() {

    @MockkBean
    private lateinit var menuDao: MenuDao

    @MockkBean
    private lateinit var pizzaMenuDao: PizzaMenuDao

    @MockkBean
    private lateinit var storageClient: StorageClient

    @MockkBean
    private lateinit var ledgerClient: LedgerClient

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    override fun beforeEach(testCase: TestCase) {
        every { menuDao.getCoffeeMenu() } returns coffeeTypes
        every { menuDao.getCoffee(any()) } answers { coffeeTypes.find { it.name == firstArg() } }
        every { pizzaMenuDao.getPizzaMenu() } returns pizzaTypes
        every { pizzaMenuDao.getPizza(any()) } answers { pizzaTypes.find { it.name == firstArg() } }
        every { storageClient.getAmount(any()) } answers { storageAmounts[firstArg()] ?: 0 }
        every { storageClient.take(any(), any()) } answers {
            storageAmounts[firstArg()] = storageAmounts.getValue(firstArg()) - arg<Int>(1)
        }
        every { ledgerClient.getIngredientPrice(any()) } answers { ingredientPrices.getValue(firstArg()) }
        every { ledgerClient.getCoffeePrice(any()) } answers { coffeePrices.getValue(firstArg<Coffee>().name) }
        every { ledgerClient.saveOrder(any()) } returns nextInt()
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

    init {
        feature("order coffee") {
            scenario("success") {
                val coffeeMenu = getCoffeeMenu()
                val coffee = coffeeMenu.first()
                val cash = coffee.price + 1

                val order = orderCoffee(coffee.name, cash)

                order should {
                    it.item!!.name shouldBe coffee.name
                    it.status shouldBe Status.READY
                    it.change shouldBe (cash - coffee.price)
                    it.comment shouldBe null
                }
            }
            scenario("failure - unknown coffee") {
                val coffeeName = "лавандовый раф"
                val cash = Double.MAX_VALUE

                val order = orderCoffee(coffeeName, cash)

                order should {
                    it.item shouldBe null
                    it.status shouldBe Status.DECLINED
                    it.change shouldBe cash
                    it.comment shouldBe "Нет такого кофе в меню!"
                }
            }
            scenario("failure - insufficient cash") {
                val coffeeMenu = getCoffeeMenu()
                val coffee = coffeeMenu.first()
                val cash = coffee.price - 1

                val order = orderCoffee(coffee.name, cash)

                order should {
                    it.item shouldBe null
                    it.status shouldBe Status.DECLINED
                    it.change shouldBe cash
                    it.comment shouldBe "Недостаточно денег!"
                }
            }
        }
        feature("order pizza") {
            scenario("success") {
                val pizzaMenu = getPizzaMenu()
                val pizza = pizzaMenu.first()
                val cash = pizza.price + 1

                val order = orderPizza(pizza.name, cash)
                val orderId = order.item

                orderId.shouldNotBeNull()
                order.change shouldBe (cash - pizza.price)

                getPizzaIfReady(orderId) shouldBe OrderResponse(status = Status.IN_PROGRESS)
                delay(1000)
                getPizzaIfReady(orderId) should {
                    it.item!!.name shouldBe pizza.name
                    it.status shouldBe Status.READY
                }
            }
            scenario("failure - unknown pizza") {
                val pizzaName = "диабло 2"
                val cash = Double.MAX_VALUE

                val order = orderPizza(pizzaName, cash)

                order should {
                    it.item shouldBe null
                    it.status shouldBe Status.DECLINED
                    it.change shouldBe cash
                    it.comment shouldBe "Нет такой пиццы в меню!"
                }
            }
            scenario("failure - insufficient cash") {
                val pizzaMenu = getPizzaMenu()
                val pizza = pizzaMenu.first()
                val cash = pizza.price - 1

                val order = orderPizza(pizza.name, cash)

                order should {
                    it.status shouldBe Status.DECLINED
                    it.item shouldBe null
                    it.change shouldBe cash
                    it.comment shouldBe "Недостаточно денег!"
                }
            }
        }
    }

    private fun getCoffeeMenu(): Set<CoffeeMenuItem> =
        mockMvc.get("/coffee/menu").readResponse()

    private fun orderCoffee(name: String, cash: Double, status: HttpStatus = HttpStatus.OK): OrderResponse<Coffee> =
        mockMvc.post("/coffee/order?name={name}&cash={cash}", name, cash).readResponse(status)

    private fun getPizzaMenu(): Set<PizzaMenuItem> =
        mockMvc.get("/pizza/menu").readResponse()

    private fun orderPizza(name: String, cash: Double, status: HttpStatus = HttpStatus.OK): OrderResponse<Int> =
        mockMvc.post("/pizza/order?name={name}&cash={cash}", name, cash).readResponse(status)

    private fun getPizzaIfReady(orderId: Int): OrderResponse<Pizza> =
        mockMvc.get("/pizza/order/{orderId}", orderId).readResponse()

    private inline fun <reified T> ResultActionsDsl.readResponse(expectedStatus: HttpStatus = HttpStatus.OK): T = this
        .andExpect { status { isEqualTo(expectedStatus.value()) } }
        .andReturn().response.getContentAsString(UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it) }

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

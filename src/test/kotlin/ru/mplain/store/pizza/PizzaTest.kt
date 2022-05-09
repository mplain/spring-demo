package ru.mplain.store.pizza

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
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
import ru.mplain.store.accounting.model.FoodType
import ru.mplain.store.accounting.model.Order
import ru.mplain.store.accounting.model.Status
import ru.mplain.store.accounting.service.client.PricesClient
import ru.mplain.store.accounting.service.client.PricesClientDev
import ru.mplain.store.pizza.model.PizzaInMenu
import java.math.BigDecimal
import kotlin.text.Charsets.UTF_8

@SpringBootTest
@AutoConfigureMockMvc
class PizzaTest(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper) : FeatureSpec() {

    @MockkBean
    private lateinit var pricesClient: PricesClient

    private val pricesClientDev = PricesClientDev()

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    override suspend fun beforeEach(testCase: TestCase) {
        every { pricesClient.getIngredientPrice(any()) } answers { pricesClientDev.getIngredientPrice(firstArg()) }
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

    init {
        feature("order pizza") {
            scenario("success") {
                val pizzaMenu = getPizzaMenu()
                val pizza = pizzaMenu.first()
                val cash = pizza.price + BigDecimal.ONE

                val order = orderPizza(pizza.name, cash)

                order should {
                    it.id shouldBeGreaterThan 0
                    it.type shouldBe FoodType.PIZZA
                    it.name shouldBe pizza.name
                    it.cash shouldBe cash
                    it.price shouldBe pizza.price
                    it.status shouldBe Status.IN_PROGRESS
                    it.comment.shouldBeNull()
                }

                getOrder(order.id) shouldBe order
                delay(1000)
                getOrder(order.id) shouldBe order.copy(status = Status.READY)
            }
            scenario("failure - unknown pizza") {
                val pizzaName = "Diablo II"
                val cash = BigDecimal(100)

                val order = orderPizza(pizzaName, cash)

                order should {
                    it.id shouldBe 0
                    it.type shouldBe FoodType.PIZZA
                    it.name shouldBe pizzaName
                    it.cash shouldBe cash
                    it.price shouldBe BigDecimal.ZERO
                    it.status shouldBe Status.DECLINED
                    it.comment shouldBe "Pizza $pizzaName not available"
                }
            }
            scenario("failure - insufficient cash") {
                val pizzaMenu = getPizzaMenu()
                val pizza = pizzaMenu.first()
                val cash = pizza.price - BigDecimal.ONE

                val order = orderPizza(pizza.name, cash)

                order should {
                    it.id shouldBe 0
                    it.type shouldBe FoodType.PIZZA
                    it.name shouldBe pizza.name
                    it.cash shouldBe cash
                    it.price shouldBe BigDecimal.ZERO
                    it.status shouldBe Status.DECLINED
                    it.comment shouldBe "Not enough money, price is ${pizza.price}"
                }
            }
        }
    }

    private fun getPizzaMenu(): List<PizzaInMenu> =
        mockMvc.get("/pizza/menu").readResponse()

    private fun orderPizza(name: String, cash: BigDecimal, status: HttpStatus = HttpStatus.OK): Order =
        mockMvc.post("/pizza/order?name={name}&cash={cash}", name, cash).readResponse(status)

    private fun getOrder(orderId: Long): Order =
        mockMvc.get("/pizza/order/{orderId}", orderId).readResponse()

    private inline fun <reified T> ResultActionsDsl.readResponse(expectedStatus: HttpStatus = HttpStatus.OK): T = this
        .andExpect { status { isEqualTo(expectedStatus.value()) } }
        .andReturn().response.getContentAsString(UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it) }
}

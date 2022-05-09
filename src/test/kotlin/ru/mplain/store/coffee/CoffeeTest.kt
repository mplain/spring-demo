package ru.mplain.store.coffee

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
import ru.mplain.store.coffee.model.CoffeeInMenu
import java.math.BigDecimal
import kotlin.text.Charsets.UTF_8

@SpringBootTest
@AutoConfigureMockMvc
class CoffeeTest(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper) : FeatureSpec() {

    @MockkBean
    private lateinit var pricesClient: PricesClient

    private val pricesClientDev = PricesClientDev()

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    override suspend fun beforeEach(testCase: TestCase) {
        every { pricesClient.getCoffeePrice(any()) } answers { pricesClientDev.getCoffeePrice(firstArg()) }
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

    init {
        feature("order coffee") {
            scenario("success") {
                val coffeeMenu = getCoffeeMenu()
                val coffee = coffeeMenu.first()
                val cash = coffee.price + BigDecimal.ONE

                val order = orderCoffee(coffee.name, cash)

                order should {
                    it.id shouldBeGreaterThan 0
                    it.type shouldBe FoodType.COFFEE
                    it.name shouldBe coffee.name
                    it.cash shouldBe cash
                    it.price shouldBe coffee.price
                    it.status shouldBe Status.READY
                    it.comment.shouldBeNull()
                }
            }
            scenario("failure - unknown coffee") {
                val coffeeName = "Lavender Raff"
                val cash = BigDecimal(100)

                val order = orderCoffee(coffeeName, cash)

                order should {
                    it.id shouldBe 0
                    it.type shouldBe FoodType.COFFEE
                    it.name shouldBe coffeeName
                    it.cash shouldBe cash
                    it.price shouldBe BigDecimal.ZERO
                    it.status shouldBe Status.DECLINED
                    it.comment shouldBe "Coffee $coffeeName not available"
                }
            }
            scenario("failure - insufficient cash") {
                val coffeeMenu = getCoffeeMenu()
                val coffee = coffeeMenu.first()
                val cash = coffee.price - BigDecimal.ONE

                val order = orderCoffee(coffee.name, cash)

                order should {
                    it.id shouldBe 0
                    it.type shouldBe FoodType.COFFEE
                    it.name shouldBe coffee.name
                    it.cash shouldBe cash
                    it.price shouldBe BigDecimal.ZERO
                    it.status shouldBe Status.DECLINED
                    it.comment shouldBe "Not enough money, price is ${coffee.price}"
                }
            }
        }
    }

    private fun getCoffeeMenu(): List<CoffeeInMenu> =
        mockMvc.get("/coffee/menu").readResponse()

    private fun orderCoffee(name: String, cash: BigDecimal, status: HttpStatus = HttpStatus.OK): Order =
        mockMvc.post("/coffee/order?name={name}&cash={cash}", name, cash).readResponse(status)

    private inline fun <reified T> ResultActionsDsl.readResponse(expectedStatus: HttpStatus = HttpStatus.OK): T = this
        .andExpect { status { isEqualTo(expectedStatus.value()) } }
        .andReturn().response.getContentAsString(UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it) }
}

package ru.mplain.store.accounting.service.client

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@Profile("dev")
class PricesClientDev : PricesClient {

    override fun getCoffeePrice(name: String): BigDecimal =
        coffeePrices.getValue(name)

    override fun getIngredientPrice(name: String): BigDecimal =
        ingredientPrices.getValue(name)

    private val ingredientPrices = mapOf(
        "dough" to 1.00,
        "eggs" to 3.48,
        "cheese" to 0.98,
        "tomato" to 1.53,
        "mushroom" to 3.34,
        "olive" to 1.53,
        "arugula" to 2.00,
        "asparagus" to 3.34,
        "bacon" to 6.48,
        "beef jerky" to 12.24,
        "meatball" to 9.38,
        "salami" to 8.00
    ).mapValues { it.value.toBigDecimal() }

    private val coffeePrices = mapOf(
        "espresso" to 5.00,
        "americano" to 4.24,
        "cappuccino" to 3.48,
        "latte" to 5.12
    ).mapValues { it.value.toBigDecimal() }
}

package ru.mplain.store.accounting.service.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.math.BigDecimal

@Service
@Profile("prod")
class PricesClientProd(
    private val restTemplate: RestTemplate,
    @Value("\${prices.address}") private val pricesAddress: String
) : PricesClient {

    override fun getCoffeePrice(name: String): BigDecimal = getPrice(TYPE_COFFEE, name)

    override fun getIngredientPrice(name: String): BigDecimal = getPrice(TYPE_INGREDIENT, name)

    private fun getPrice(type: String, name: String): BigDecimal =
        restTemplate.getForObject(pricesAddress + GET_PRICE, type, name)
}

private const val GET_PRICE = "/price?type={type}&name={name}"

private const val TYPE_INGREDIENT = "ingredient"
private const val TYPE_COFFEE = "coffee"

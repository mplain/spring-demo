package ru.tinkoff.fintech.pizza.service.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject
import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.external.SaveOrderRequest
import ru.tinkoff.fintech.pizza.model.external.SaveOrderResponse

@Service
class Ledger(
    private val restTemplate: RestTemplate,
    @Value("\${ledger.address}") private val ledgerAddress: String
) {

    fun getIngredientPrice(item: String): Double = getPrice(TYPE_INGREDIENT, item)

    fun getCoffeePrice(coffee: Coffee): Double = getPrice(TYPE_COFFEE, coffee.name)

    fun getPrice(type: String, name: String): Double =
        restTemplate.getForObject("$ledgerAddress$GET_PRICE", type, name)!!

    fun saveOrder(price: Double): Int {
        val request = SaveOrderRequest(price)
        return restTemplate.postForObject<SaveOrderResponse?>("$ledgerAddress$SAVE_ORDER", request)!!.orderId
    }
}

private const val GET_PRICE = "/price?type={type}&name={name}"
private const val SAVE_ORDER = "/order"

private const val TYPE_INGREDIENT = "ingredient"
private const val TYPE_COFFEE = "coffee"

package ru.tinkoff.fintech.pizza.service.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForEntity
import ru.tinkoff.fintech.external.model.GetAmountResponse
import ru.tinkoff.fintech.external.model.TakeIngredientRequest

@Service
class StorageClient(
    private val restTemplate: RestTemplate,
    @Value("\${storage.address}") private val storageAddress: String
) {

    fun getAmount(ingredient: String): Int =
        restTemplate.getForObject<GetAmountResponse>("$storageAddress$GET_INGREDIENT_AMOUNT", ingredient).amount

    fun take(ingredient: String, amount: Int) {
        val request = TakeIngredientRequest(ingredient, amount)
        restTemplate.postForEntity<Void>("$storageAddress$TAKE_INGREDIENT", request)
    }
}

private const val GET_INGREDIENT_AMOUNT = "/ingredients/{ingredient}"
private const val TAKE_INGREDIENT = "/ingredients/take"

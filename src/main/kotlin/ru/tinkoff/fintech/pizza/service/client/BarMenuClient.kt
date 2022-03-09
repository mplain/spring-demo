package ru.tinkoff.fintech.pizza.service.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.NotFound
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForObject
import ru.tinkoff.fintech.pizza.model.Coffee

@Service
class BarMenuClient(
    private val restTemplate: RestTemplate,
    @Value("bar.menu.address") private val barMenuAddress: String
) {

    fun getCoffeeMenu(): Set<Coffee> =
        restTemplate.exchange<Set<Coffee>>("$barMenuAddress$GET_COFFEE_MENU", HttpMethod.GET).body.orEmpty()

    fun getCoffee(name: String): Coffee? = try {
        restTemplate.getForObject("$barMenuAddress$GET_COFFEE_BY_NAME", name.lowercase())
    } catch (e: NotFound) {
        null
    }
}

private const val GET_COFFEE_MENU = "/coffee"
private const val GET_COFFEE_BY_NAME = "/coffee/{name}"

package ru.tinkoff.fintech.pizza.service.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.NotFound
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForObject
import ru.tinkoff.fintech.pizza.model.Pizza

@Service
class PizzaMenuClient(
    private val restTemplate: RestTemplate,
    @Value("\${pizza.menu.address}") private val pizzaMenuAddress: String
) {

    fun getPizzaMenu(): Set<Pizza> =
        restTemplate.exchange<Set<Pizza>>("$pizzaMenuAddress$GET_PIZZA_MENU", HttpMethod.GET).body.orEmpty()

    fun getPizza(name: String): Pizza? = try {
        restTemplate.getForObject("$pizzaMenuAddress$GET_PIZZA_BY_NAME", name.lowercase())
    } catch (e: NotFound) {
        null
    }
}

private const val GET_PIZZA_MENU = "/pizza"
private const val GET_PIZZA_BY_NAME = "/pizza?name={name}"

package ru.tinkoff.fintech.pizza.service.client

import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.pizza.model.Coffee
import ru.tinkoff.fintech.pizza.model.Pizza

@Service
class MenuDao(private val jdbcTemplate: JdbcTemplate) {

    fun getCoffeeMenu(): Set<Coffee> = jdbcTemplate.query(GET_COFFEE_MENU, coffeeMapper).toSet()

    fun getCoffee(name: String): Coffee? = jdbcTemplate.queryForObject(GET_COFFEE_BY_NAME, coffeeMapper, name)

    fun getPizzaMenu(): Set<Pizza> = jdbcTemplate.query(GET_PIZZA_MENU, pizzaMapper).toSet()

    fun getPizza(name: String): Pizza? = jdbcTemplate.queryForObject(GET_PIZZA_BY_NAME, pizzaMapper, name)
}

private val coffeeMapper = DataClassRowMapper(Coffee::class.java)
private val pizzaMapper = DataClassRowMapper(Pizza::class.java)

@Language("Sql")
private const val GET_COFFEE_MENU = "select * from menu.coffee"

@Language("Sql")
private const val GET_COFFEE_BY_NAME = "select * from menu.coffee where name = ?"

@Language("Sql")
private const val GET_PIZZA_MENU = "select * from menu.pizza"

@Language("Sql")
private const val GET_PIZZA_BY_NAME = "select * from menu.pizza where name = ?"

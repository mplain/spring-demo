package ru.mplain.store.pizza.service.dao

import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ru.mplain.store.pizza.model.Pizza
import ru.mplain.store.pizza.model.PizzaIngredient

@Service
class PizzaMenuDao(private val jdbcTemplate: JdbcTemplate) {

    fun getPizzaMenu(): List<String> =
        jdbcTemplate.queryForList(GET_PIZZA_MENU, String::class.java)

    fun getPizza(name: String): Pizza? =
        jdbcTemplate.query(GET_PIZZA_INGREDIENTS, pizzaIngredientMapper, name.lowercase())
            .takeIf { it.isNotEmpty() }
            ?.let { Pizza(name, it) }
}

private val pizzaIngredientMapper = DataClassRowMapper(PizzaIngredient::class.java)

@Language("Sql")
private const val GET_PIZZA_MENU = "select * from menu.pizza"

@Language("Sql")
private const val GET_PIZZA_INGREDIENTS = "select * from menu.pizza_ingredients where pizza = ?"

package ru.mplain.store.coffee.service.dao

import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ru.mplain.store.coffee.model.Coffee

@Service
class CoffeeMenuDao(private val jdbcTemplate: JdbcTemplate) {

    fun getCoffeeMenu(): List<Coffee> =
        jdbcTemplate.query(GET_COFFEE_MENU, coffeeMapper)

    fun getCoffee(name: String): Coffee? =
        jdbcTemplate.queryForObject(GET_COFFEE_BY_NAME, coffeeMapper, name.lowercase())
}

private val coffeeMapper = DataClassRowMapper(Coffee::class.java)

@Language("Sql")
private const val GET_COFFEE_MENU = "select * from menu.coffee"

@Language("Sql")
private const val GET_COFFEE_BY_NAME = "select * from menu.coffee where name = ?"

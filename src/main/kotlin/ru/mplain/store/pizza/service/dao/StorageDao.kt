package ru.mplain.store.pizza.service.dao

import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import ru.mplain.store.pizza.model.Ingredient

@Service
class StorageDao(jdbcTemplate: JdbcTemplate) {

    private val namedJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

    fun getIngredients(ingredients: List<String>): List<Ingredient> {
        val sqlParams = MapSqlParameterSource("names", ingredients)
        return namedJdbcTemplate.query(GET_INGREDIENTS, sqlParams, ingredientMapper)
    }

    fun takeIngredients(ingredients: Map<String, Int>): IntArray {
        val sqlParams = ingredients
            .map { MapSqlParameterSource("name", it.key).addValue("amount", it.value) }
            .toTypedArray()
        return namedJdbcTemplate.batchUpdate(TAKE_INGREDIENT, sqlParams)
    }
}

private val ingredientMapper = DataClassRowMapper(Ingredient::class.java)

@Language("Sql")
private const val GET_INGREDIENTS =
    "select * from storage.ingredient where name in (:names)"

@Language("Sql")
private const val TAKE_INGREDIENT =
    "update storage.ingredient set amount = amount - :amount where name = :name and amount >= :amount"

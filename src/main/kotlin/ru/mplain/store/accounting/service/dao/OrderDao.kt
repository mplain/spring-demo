package ru.mplain.store.accounting.service.dao

import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Service
import ru.mplain.store.accounting.model.Order

@Service
class OrderDao(private val jdbcTemplate: JdbcTemplate) {

    private val insertOrder = SimpleJdbcInsert(jdbcTemplate)
        .withSchemaName(SCHEMA_ACCOUNTING)
        .withTableName(TABLE_ORDERS)
        .usingGeneratedKeyColumns("id")

    fun saveOrder(order: Order): Long {
        val sqlParams = BeanPropertySqlParameterSource(order)
        return insertOrder.executeAndReturnKey(sqlParams) as Long
    }

    fun updateOrder(order: Order) {
        jdbcTemplate.update(UPDATE_ORDER, order.status.name, order.comment, order.id)
    }

    fun getOrder(orderId: Long): Order? =
        jdbcTemplate.queryForObject(GET_ORDER, orderMapper, orderId)
}

private val orderMapper = DataClassRowMapper(Order::class.java)

private const val SCHEMA_ACCOUNTING = "accounting"
private const val TABLE_ORDERS = "orders"

@Language("Sql")
private const val UPDATE_ORDER =
    "update accounting.orders set status = ?, comment = ?, updated = current_timestamp where id = ?"

@Language("Sql")
private const val GET_ORDER = "select * from accounting.orders where id = ?"

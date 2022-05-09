package ru.mplain.store.accounting.service.client

import java.math.BigDecimal

interface PricesClient {

    fun getCoffeePrice(name: String): BigDecimal

    fun getIngredientPrice(name: String): BigDecimal
}

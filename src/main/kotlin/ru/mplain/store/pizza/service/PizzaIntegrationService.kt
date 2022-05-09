package ru.mplain.store.pizza.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.mplain.store.accounting.service.client.PricesClient
import ru.mplain.store.pizza.model.Pizza
import ru.mplain.store.pizza.service.dao.PizzaMenuDao
import ru.mplain.store.pizza.service.dao.StorageDao
import java.math.BigDecimal

@Service
class PizzaIntegrationService(
    private val pizzaMenuDao: PizzaMenuDao,
    private val storageDao: StorageDao,
    private val pricesClient: PricesClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getPizzaMenu(): Map<Pizza, BigDecimal> =
        pizzaMenuDao.getPizzaMenu()
            .mapNotNull(::getPizza)
            .associate { (pizza, price) -> pizza to price }

    @Cacheable("pizza")
    fun getPizza(name: String): Pair<Pizza, BigDecimal>? =
        pizzaMenuDao.getPizza(name)
            ?.takeIf(::hasEnoughIngredients)
            ?.let { it to getPizzaPrice(it) }

    @Transactional
    fun takeIngredients(pizza: Pizza) {
        check(hasEnoughIngredients(pizza)) { "Insufficient ingredients" }
        val result = storageDao.takeIngredients(pizza.ingredients)
        check(result.all { it != 0 }) { "Insufficient ingredients, rollback transaction" }
    }

    @CacheEvict("pizza", key = "#pizza")
    fun insufficientIngredients(pizza: String) {
    }

    private fun hasEnoughIngredients(pizza: Pizza): Boolean {
        val ingredientNames = pizza.ingredients.map { it.key }
        val ingredientAmounts = storageDao.getIngredients(ingredientNames)
        val missingIngredients = pizza.ingredients.filter { (ingredient, amountNeeded) ->
            val amountPresent = ingredientAmounts.find { it.name == ingredient }?.amount ?: 0
            amountNeeded > amountPresent
        }
        if (missingIngredients.isNotEmpty()) {
            log.warn("Insufficient ingredients $missingIngredients to make pizza ${pizza.name}")
        }
        return missingIngredients.isEmpty()
    }

    private fun getPizzaPrice(pizza: Pizza): BigDecimal =
        pizza.ingredients
            .map { (ingredient, amount) -> pricesClient.getIngredientPrice(ingredient) * amount.toBigDecimal() }
            .reduce(BigDecimal::add)
}

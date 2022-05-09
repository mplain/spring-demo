package ru.mplain.store.coffee.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.mplain.store.accounting.service.client.PricesClient
import ru.mplain.store.coffee.model.Coffee
import ru.mplain.store.coffee.service.dao.CoffeeMenuDao
import java.math.BigDecimal

@Service
class CoffeeIntegrationService(
    private val coffeeMenuDao: CoffeeMenuDao,
    private val pricesClient: PricesClient
) {

    @Cacheable("coffeeMenu")
    fun getCoffeeMenu(): Map<Coffee, BigDecimal> =
        coffeeMenuDao.getCoffeeMenu()
            .associateWith { pricesClient.getCoffeePrice(it.name) }
}

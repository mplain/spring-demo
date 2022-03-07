package ru.tinkoff.fintech.pizza.service.client

class Storage {

    private val storage: MutableMap<String, Int> = mutableMapOf(
        "тесто" to 20,
        "яйца" to 20,
        "сыр" to 20,
        "пармезан" to 20,
        "томат" to 20,
        "оливки" to 20,
        "зелень" to 20,
        "грибы" to 20,
        "спаржа" to 20,
        "бекон" to 20,
        "салями" to 20,
        "вяленая говядина" to 20,
        "мясное ассорти" to 20
    )

    fun take(ingredients: Map<String, Int>) {
        check(hasEnough(ingredients)) { "Недостаточно ингредиентов!" }
        ingredients.forEach { (ingredient, amount) -> take(ingredient, amount) }
    }

    private fun hasEnough(ingredients: Map<String, Int>): Boolean =
        ingredients.all { (ingredient, amount) -> getAmount(ingredient) >= amount }

    private fun getAmount(ingredient: String): Int = storage[ingredient] ?: 0

    private fun take(ingredient: String, amount: Int) {
        val currentAmount = getAmount(ingredient)
        check(currentAmount >= amount) { "Недостаточно ингредиентов!" }
        storage[ingredient] = currentAmount - amount
    }
}

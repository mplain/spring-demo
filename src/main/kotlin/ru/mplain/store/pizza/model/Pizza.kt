package ru.mplain.store.pizza.model

data class Pizza(
    val name: String,
    val ingredients: Map<String, Int>
) {

    constructor(name: String, ingredients: List<PizzaIngredient>) : this(
        name = name,
        ingredients = ingredients.associate { it.ingredient to it.amount }
    )
}

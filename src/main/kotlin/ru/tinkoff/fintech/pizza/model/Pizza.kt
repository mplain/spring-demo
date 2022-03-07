package ru.tinkoff.fintech.pizza.model

data class Pizza(
    override val name: String,
    val ingredients: Map<String, Int>
) : Food

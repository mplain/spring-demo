package ru.tinkoff.fintech.external.model

data class GetAmountResponse(val ingredient: String, val amount: Int)

data class TakeIngredientRequest(val ingredient: String, val amount: Int)

package ru.tinkoff.fintech.pizza.cancellation

import io.kotest.core.spec.style.FeatureSpec

class PizzaStoreTest : FeatureSpec() {

    init {
        feature("pizza store test") {
            scenario("order pizza success") {
                println("success")
            }
            scenario("order pizza failure") {
                println("failure")
            }
        }
    }
}

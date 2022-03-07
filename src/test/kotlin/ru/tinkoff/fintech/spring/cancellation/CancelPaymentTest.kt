package ru.tinkoff.fintech.spring.cancellation

import io.kotest.core.spec.style.FeatureSpec
import ru.tinkoff.fintech.spring.cancellation.model.CancelPaymentRequest

class CancelPaymentTest : FeatureSpec() {

    private val controller = CancelPaymentController()

    init {
        feature("cancel payment") {
            scenario("successful") {
                cancelPayment(request)
            }
        }
    }

    private fun cancelPayment(request: CancelPaymentRequest) {
        controller.cancelPayment(request)
    }

    private val request = CancelPaymentRequest(1, "comment")
}

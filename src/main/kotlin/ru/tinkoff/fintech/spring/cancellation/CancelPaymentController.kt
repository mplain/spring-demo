package ru.tinkoff.fintech.spring.cancellation

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tinkoff.fintech.spring.cancellation.model.CancelPaymentRequest
import ru.tinkoff.fintech.spring.cancellation.service.CancelPaymentService

@RestController
@RequestMapping("/demo")
class CancelPaymentController {

    private val cancelPaymentService = CancelPaymentService()

    @PostMapping("/cancel-payment")
    fun cancelPayment(@RequestBody request: CancelPaymentRequest) {
        cancelPaymentService.cancelPayment(request)
    }
}

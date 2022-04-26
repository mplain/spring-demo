package ru.tinkoff.fintech.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("external.address")
@ConstructorBinding
data class ExternalAddresses(
    val bar: String,
    val menu: String,
    val storage: String,
    val ledger: String
)

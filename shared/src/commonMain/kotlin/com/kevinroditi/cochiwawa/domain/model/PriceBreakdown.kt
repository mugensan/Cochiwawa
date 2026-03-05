package com.kevinroditi.cochiwawa.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.kevinroditi.cochiwawa.core.util.BigDecimalSerializer
import kotlinx.serialization.Serializable

@Serializable
data class PriceBreakdown(
    @Serializable(with = BigDecimalSerializer::class)
    val gasCost: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val tollCost: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val maintenanceCost: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val basePrice: BigDecimal,
    val justificationNote: String? = null
) {
    val serviceFee: BigDecimal = basePrice.multiply(BigDecimal.fromDouble(0.08))
    val finalPrice: BigDecimal = basePrice.add(serviceFee)
}

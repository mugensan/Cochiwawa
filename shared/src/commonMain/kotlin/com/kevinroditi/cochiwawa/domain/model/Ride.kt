package com.kevinroditi.cochiwawa.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Ride(
    val id: String,
    val driverId: String,
    val driverName: String,
    val origin: String,
    val destination: String,
    val departureTime: Instant,
    val basePrice: BigDecimal,
    val gasCost: BigDecimal,
    val tollCost: BigDecimal,
    val maintenanceCost: BigDecimal,
    val justificationNote: String? = null,
    val availableSeats: Int,
    val totalSeats: Int,
    val currency: String = "EUR"
) {
    val serviceFee: BigDecimal = basePrice.multiply(BigDecimal.fromDouble(0.08))
    val finalPrice: BigDecimal = basePrice.add(serviceFee)
}

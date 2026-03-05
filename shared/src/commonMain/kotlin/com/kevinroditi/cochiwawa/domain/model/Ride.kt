package com.kevinroditi.cochiwawa.domain.model

import com.kevinroditi.cochiwawa.core.util.BigDecimalSerializer
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
    val breakdown: PriceBreakdown,
    val availableSeats: Int
)

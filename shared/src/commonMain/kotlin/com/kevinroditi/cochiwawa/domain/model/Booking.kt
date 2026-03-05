package com.kevinroditi.cochiwawa.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

@Serializable
data class Booking(
    val id: String,
    val rideId: String,
    val passengerId: String,
    val totalPaid: BigDecimal,
    val commissionAmount: BigDecimal,
    val driverPayout: BigDecimal,
    val status: BookingStatus,
    val createdAt: Instant
)

package com.kevinroditi.cochiwawa.domain.model

data class RecurringRide(
    val id: String,
    val driverId: String,
    val corridorId: String,
    val departureTime: String,
    val daysOfWeek: List<String>,
    val seats: Int,
    val pricePerSeat: Double
)

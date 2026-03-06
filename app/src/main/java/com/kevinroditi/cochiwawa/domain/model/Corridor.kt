package com.kevinroditi.cochiwawa.domain.model

data class Corridor(
    val id: String,
    val name: String,
    val origin: String,
    val destination: String,
    val activeRides: Int,
    val nextDeparture: String? = null,
    val seatsLeft: Int? = null,
    val pricePerSeat: Double? = null
)

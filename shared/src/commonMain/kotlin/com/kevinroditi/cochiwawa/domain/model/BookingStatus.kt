package com.kevinroditi.cochiwawa.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

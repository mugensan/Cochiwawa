package com.kevinroditi.cochiwawa.domain.model

data class ChatRoom(
    val id: String,
    val rideId: String,
    val driverName: String,
    val routeName: String,
    val tripDate: String,
    val lastMessage: String?,
    val isTripCompleted: Boolean = false
)

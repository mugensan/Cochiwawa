package com.kevinroditi.cochiwawa.domain.model

data class Message(
    val id: String,
    val chatRoomId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val createdAt: Long
)

package com.kevinroditi.cochiwawa.domain.model

data class Subscription(
    val id: String,
    val userId: String,
    val planType: String,
    val validUntil: String
)

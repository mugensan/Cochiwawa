package com.kevinroditi.cochiwawa.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole { PASSENGER, DRIVER }

@Serializable
data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val rating: Double = 5.0
)

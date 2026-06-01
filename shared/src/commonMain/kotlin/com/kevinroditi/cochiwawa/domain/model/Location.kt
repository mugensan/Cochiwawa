package com.kevinroditi.cochiwawa.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: String,
    val name: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)

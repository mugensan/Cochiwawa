package com.cochiwawa.shared

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String>? = null
)

@Serializable
data class Passenger(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class Driver(
    val id: Int,
    val name: String,
    val vehicleInfo: String
)

@Serializable
data class Ride(
    val id: Int,
    val origin: String,
    val destination: String,
    val availableSeats: Int,
    val price: Double
)

@Serializable
data class Payment(
    val id: Int,
    val amount: Double,
    val fee: Double,
    val status: String
)

@Serializable
data class GraphQLResponse<T>(
    val data: T? = null,
    val errors: List<GraphQLError>? = null
)

@Serializable
data class GraphQLError(
    val message: String
)

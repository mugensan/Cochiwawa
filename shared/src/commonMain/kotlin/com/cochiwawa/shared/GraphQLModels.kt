package com.cochiwawa.shared

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String>? = null
)

@Serializable
data class UserRemote(
    val id: String,
    val email: String,
    val role: String,
    val fullName: String? = null,
    val gender: String? = null,
    val nationalId: String? = null,
    val profilePhotoUrl: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserRemote? = null
)

@Serializable
data class RegisterData(val signUp: AuthResponse)

@Serializable
data class LoginData(val signIn: AuthResponse)

@Serializable
data class Booking(
    val id: String,
    val passengerId: String,
    val rideId: Int,
    val seats: Int,
    val createdAt: String,
    val ride: Ride? = null
)

@Serializable
data class Earnings(
    val totalEarnings: Double,
    val totalRides: Int
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
    val departureTime: String,
    val availableSeats: Int,
    val pricePerSeat: Double
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

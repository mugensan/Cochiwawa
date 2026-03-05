package com.cochiwawa.shared

import kotlinx.serialization.Serializable

@Serializable
data class Passenger(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null
)

@Serializable
data class Driver(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val vehicleModel: String,
    val vehiclePlate: String
)

@Serializable
data class Ride(
    val id: String,
    val driverId: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val availableSeats: Int,
    val pricePerSeat: Double,
    val driverCost: Double = 0.0,
    val distance: Double = 0.0
)

@Serializable
data class Booking(
    val id: String,
    val rideId: String,
    val passengerId: String,
    val seatsBooked: Int,
    val totalPaid: Double,
    val platformFee: Double,
    val driverAmount: Double,
    val status: String
)

@Serializable
data class Payment(
    val id: String,
    val rideId: String,
    val passengerId: String,
    val amount: Double,
    val status: String = "SUCCESS"
)

@Serializable
data class PriceSuggestion(
    val distance: Double,
    val seats: Int,
    val driverCost: Double,
    val suggestedFare: Double
)

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>? = null
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

@Serializable
data class PassengerNodes(val nodes: List<Passenger>)
@Serializable
data class AllPassengers(val allPassengers: PassengerNodes)

@Serializable
data class DriverNodes(val nodes: List<Driver>)
@Serializable
data class AllDrivers(val allDrivers: DriverNodes)

@Serializable
data class RideNodes(val nodes: List<Ride>)
@Serializable
data class AllRides(val allRides: RideNodes)

@Serializable
data class PaymentNodes(val nodes: List<Payment>)
@Serializable
data class AllPayments(val allPayments: PaymentNodes)

@Serializable
data class CreateRideData(val createRide: Ride)
@Serializable
data class CreateBookingData(val createBooking: Booking)

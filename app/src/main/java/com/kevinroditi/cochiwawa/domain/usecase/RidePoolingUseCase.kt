package com.kevinroditi.cochiwawa.domain.usecase

import com.kevinroditi.cochiwawa.domain.model.Route
import kotlin.math.*

data class RideRequest(
    val passengerId: String,
    val route: Route,
    val seats: Int
)

data class DriverRide(
    val rideId: String,
    val route: Route,
    val availableSeats: Int,
    val pricePerSeat: Double
)

class RidePoolingUseCase {

    fun findMatches(
        request: RideRequest,
        availableRides: List<DriverRide>
    ): List<DriverRide> {
        return availableRides.filter { ride ->
            val originDistance = distance(
                request.route.originLat, request.route.originLng,
                ride.route.originLat, ride.route.originLng
            )
            val destinationDistance = distance(
                request.route.destinationLat, request.route.destinationLng,
                ride.route.destinationLat, ride.route.destinationLng
            )

            // Match if pickup and dropoff are within 10km of the driver's route points
            originDistance < 10.0 && destinationDistance < 10.0 && ride.availableSeats >= request.seats
        }
    }

    private fun distance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371.0 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}

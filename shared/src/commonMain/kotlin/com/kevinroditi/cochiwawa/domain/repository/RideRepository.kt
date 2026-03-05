package com.kevinroditi.cochiwawa.domain.repository

import com.kevinroditi.cochiwawa.domain.model.Ride
import kotlinx.datetime.Instant

interface RideRepository {
    suspend fun searchRides(origin: String, destination: String, date: Instant): List<Ride>
    suspend fun getRideById(id: String): Ride?
    suspend fun createRide(ride: Ride): Result<Unit>
    suspend fun updateAvailableSeats(rideId: String, newSeats: Int): Result<Unit>
}

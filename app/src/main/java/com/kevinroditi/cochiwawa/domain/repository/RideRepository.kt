package com.kevinroditi.cochiwawa.domain.repository

import com.cochiwawa.shared.Ride

interface RideRepository {
    suspend fun searchAvailableRides(origin: String, destination: String, seats: Int): Result<List<Ride>>
    suspend fun createRide(origin: String, destination: String, seats: Int, price: Double, genderPref: String): Result<Boolean>
    suspend fun getRideDetails(rideId: String): Result<Ride>
    suspend fun startRide(rideId: String): Result<Boolean>
    suspend fun completeRide(rideId: String): Result<Boolean>
    suspend fun submitRating(rideId: String, rating: Int, comment: String?): Result<Boolean>
}

package com.kevinroditi.cochiwawa.domain.repository

import com.cochiwawa.shared.Ride
import com.kevinroditi.cochiwawa.domain.model.Corridor
import com.kevinroditi.cochiwawa.domain.model.RecurringRide
import com.kevinroditi.cochiwawa.domain.model.Subscription

interface RideRepository {
    suspend fun searchAvailableRides(
        origin: String, 
        destination: String, 
        seats: Int,
        windowMinutes: Int = 20
    ): Result<List<Ride>>

    suspend fun createRide(origin: String, destination: String, seats: Int, price: Double, genderPref: String): Result<Boolean>
    suspend fun getRideDetails(rideId: String): Result<Ride>
    suspend fun startRide(rideId: String): Result<Boolean>
    suspend fun completeRide(rideId: String): Result<Boolean>
    suspend fun submitRating(rideId: String, rating: Int, comment: String?): Result<Boolean>
    
    // Phase 11 & 12
    suspend fun getCorridors(): Result<List<Corridor>>
    suspend fun createCorridorRide(corridorId: String, departureTime: String, seats: Int, price: Double): Result<Boolean>
    suspend fun createRecurringRide(recurringRide: RecurringRide): Result<Boolean>
    
    // Phase 13
    suspend fun getSubscriptions(): Result<List<Subscription>>
    suspend fun subscribe(planType: String): Result<Boolean>
}

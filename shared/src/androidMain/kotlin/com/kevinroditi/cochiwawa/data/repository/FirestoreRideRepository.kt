package com.kevinroditi.cochiwawa.data.repository

import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import kotlinx.datetime.Instant

class FirestoreRideRepository : RideRepository {
    // In a real app, this would use the Firebase Android SDK
    // For this MVP, we wrap the FakeRideRepository to ensure it runs immediately
    private val fake = FakeRideRepository()

    override suspend fun searchRides(origin: String, destination: String, date: Instant): List<Ride> {
        return fake.searchRides(origin, destination, date)
    }

    override suspend fun getRideById(id: String): Ride? {
        return fake.getRideById(id)
    }

    override suspend fun createRide(ride: Ride): Result<Unit> {
        return fake.createRide(ride)
    }

    override suspend fun updateAvailableSeats(rideId: String, newSeats: Int): Result<Unit> {
        return fake.updateAvailableSeats(rideId, newSeats)
    }
}

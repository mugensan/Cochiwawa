package com.kevinroditi.cochiwawa.data.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

class FakeRideRepository : RideRepository {
    private val mutex = Mutex()
    private val rides = mutableListOf<Ride>()

    init {
        val now = Clock.System.now()
        rides.add(
            Ride(
                id = "1",
                driverId = "driver1",
                driverName = "Kevin Roditi",
                origin = "Paris",
                destination = "Lyon",
                departureTime = now.plus(2.hours),
                basePrice = BigDecimal.fromInt(30),
                gasCost = BigDecimal.fromInt(15),
                tollCost = BigDecimal.fromInt(10),
                maintenanceCost = BigDecimal.fromInt(5),
                justificationNote = "Direct trip, high comfort",
                availableSeats = 3,
                totalSeats = 4
            )
        )
        rides.add(
            Ride(
                id = "2",
                driverId = "driver2",
                driverName = "Alice Smith",
                origin = "Paris",
                destination = "Marseille",
                departureTime = now.plus(5.hours),
                basePrice = BigDecimal.fromInt(50),
                gasCost = BigDecimal.fromInt(25),
                tollCost = BigDecimal.fromInt(15),
                maintenanceCost = BigDecimal.fromInt(10),
                justificationNote = "Includes snacks",
                availableSeats = 1,
                totalSeats = 4
            )
        )
    }

    override suspend fun searchRides(origin: String, destination: String, date: Instant): List<Ride> = mutex.withLock {
        rides.filter {
            it.origin.contains(origin, ignoreCase = true) &&
            it.destination.contains(destination, ignoreCase = true)
        }.sortedBy { it.departureTime }
    }

    override suspend fun getRideById(id: String): Ride? = mutex.withLock {
        rides.find { it.id == id }
    }

    override suspend fun createRide(ride: Ride): Result<Unit> = mutex.withLock {
        rides.add(ride)
        Result.success(Unit)
    }

    override suspend fun updateAvailableSeats(rideId: String, newSeats: Int): Result<Unit> = mutex.withLock {
        val index = rides.indexOfFirst { it.id == rideId }
        if (index != -1) {
            rides[index] = rides[index].copy(availableSeats = newSeats)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Ride not found"))
        }
    }
}

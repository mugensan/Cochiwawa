package com.kevinroditi.cochiwawa.data.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.kevinroditi.cochiwawa.domain.model.Booking
import com.kevinroditi.cochiwawa.domain.model.BookingStatus
import com.kevinroditi.cochiwawa.domain.repository.BookingRepository
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class FakeBookingRepository(
    private val rideRepository: RideRepository
) : BookingRepository {
    private val mutex = Mutex()
    private val bookings = mutableListOf<Booking>()

    override suspend fun createBooking(
        rideId: String,
        passengerId: String,
        totalPaid: BigDecimal,
        commission: BigDecimal,
        payout: BigDecimal
    ): Result<Booking> = mutex.withLock {
        val ride = rideRepository.getRideById(rideId)
            ?: return Result.failure(Exception("Ride not found"))

        if (ride.availableSeats <= 0) {
            return Result.failure(Exception("No seats available"))
        }

        // Simulate seat decrement
        rideRepository.updateAvailableSeats(rideId, ride.availableSeats - 1)

        val booking = Booking(
            id = (bookings.size + 1).toString(),
            rideId = rideId,
            passengerId = passengerId,
            totalPaid = totalPaid,
            commissionAmount = commission,
            driverPayout = payout,
            status = BookingStatus.CONFIRMED,
            createdAt = Clock.System.now()
        )
        bookings.add(booking)
        Result.success(booking)
    }

    override suspend fun getBookingsByPassenger(passengerId: String): List<Booking> = mutex.withLock {
        bookings.filter { it.passengerId == passengerId }
    }

    override suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit> = mutex.withLock {
        val index = bookings.indexOfFirst { it.id == id }
        if (index != -1) {
            bookings[index] = bookings[index].copy(status = status)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Booking not found"))
        }
    }
}

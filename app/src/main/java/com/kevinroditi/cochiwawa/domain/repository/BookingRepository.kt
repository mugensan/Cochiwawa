package com.kevinroditi.cochiwawa.domain.repository

import com.cochiwawa.shared.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun bookRide(rideId: Int, seats: Int): Result<Booking>
    suspend fun getPassengerBookings(passengerId: String): Result<List<Booking>>
}

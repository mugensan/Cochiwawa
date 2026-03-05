package com.kevinroditi.cochiwawa.domain.usecase

import com.kevinroditi.cochiwawa.domain.model.Booking
import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.repository.BookingRepository

class BookSeatUseCase(private val repository: BookingRepository) {
    suspend operator fun invoke(ride: Ride, passengerId: String): Result<Booking> {
        return repository.createBooking(
            rideId = ride.id,
            passengerId = passengerId,
            totalPaid = ride.finalPrice,
            commission = ride.serviceFee,
            payout = ride.basePrice
        )
    }
}

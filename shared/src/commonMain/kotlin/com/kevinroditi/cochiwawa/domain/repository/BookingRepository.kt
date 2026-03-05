package com.kevinroditi.cochiwawa.domain.repository

import com.kevinroditi.cochiwawa.domain.model.Booking
import com.kevinroditi.cochiwawa.domain.model.BookingStatus
import com.ionspin.kotlin.bignum.decimal.BigDecimal

interface BookingRepository {
    suspend fun createBooking(
        rideId: String,
        passengerId: String,
        totalPaid: BigDecimal,
        commission: BigDecimal,
        payout: BigDecimal
    ): Result<Booking>
    
    suspend fun getBookingsByPassenger(passengerId: String): List<Booking>
    suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit>
}

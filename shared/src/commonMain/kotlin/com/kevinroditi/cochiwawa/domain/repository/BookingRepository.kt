package com.kevinroditi.cochiwawa.domain.repository

import com.kevinroditi.cochiwawa.domain.model.Booking
import com.ionspin.kotlin.bignum.decimal.BigDecimal

interface BookingRepository {
    suspend fun bookRide(
        rideId: String,
        passengerId: String,
        totalPaid: BigDecimal,
        commissionAmount: BigDecimal,
        driverPayout: BigDecimal
    ): Result<Booking>
    
    suspend fun getBookingsForUser(userId: String): List<Booking>
}

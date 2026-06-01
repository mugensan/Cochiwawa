package com.cochiwawa.shared

object BookingLogic {
    fun calculateSplit(totalPaid: Double): Pair<Double, Double> {
        val platformFee = totalPaid * 0.08
        val driverAmount = totalPaid - platformFee
        return Pair(platformFee, driverAmount)
    }
}

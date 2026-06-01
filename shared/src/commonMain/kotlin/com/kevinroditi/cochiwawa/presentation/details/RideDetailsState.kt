package com.kevinroditi.cochiwawa.presentation.details

import com.kevinroditi.cochiwawa.domain.model.Ride

data class RideDetailsState(
    val ride: Ride? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBooking: Boolean = false,
    val bookingSuccess: Boolean = false
)

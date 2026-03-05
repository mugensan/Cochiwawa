package com.kevinroditi.cochiwawa.presentation.details

import com.kevinroditi.cochiwawa.domain.usecase.BookSeatUseCase
import com.kevinroditi.cochiwawa.domain.usecase.GetRideByIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RideDetailsViewModel(
    private val getRideByIdUseCase: GetRideByIdUseCase,
    private val bookSeatUseCase: BookSeatUseCase
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(RideDetailsState())
    val state: StateFlow<RideDetailsState> = _state.asStateFlow()

    fun loadRide(id: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val ride = getRideByIdUseCase(id)
                if (ride != null) {
                    _state.update { it.copy(ride = ride, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Ride not found") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun bookRide(passengerId: String) {
        val ride = _state.value.ride ?: return
        _state.update { it.copy(isBooking = true) }
        
        viewModelScope.launch {
            val result = bookSeatUseCase(ride, passengerId)
            result.onSuccess {
                _state.update { it.copy(isBooking = false, bookingSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isBooking = false, error = e.message) }
            }
        }
    }
}

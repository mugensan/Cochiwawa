package com.kevinroditi.cochiwawa.presentation.rides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cochiwawa.shared.Ride
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val rides: List<Ride> = emptyList(),
    val uiRides: List<Ride> = emptyList(), // Added for UI consumption
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchRideViewModel @Inject constructor(
    private val repository: RideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun searchRides(origin: String, destination: String, seats: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.searchAvailableRides(origin, destination, seats)
            result.onSuccess { rides ->
                _uiState.value = _uiState.value.copy(
                    rides = rides,
                    uiRides = rides,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
            }
        }
    }
}

package com.kevinroditi.cochiwawa.presentation.search

import com.kevinroditi.cochiwawa.domain.usecase.SearchRidesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SearchViewModel(
    private val searchRidesUseCase: SearchRidesUseCase
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onOriginChanged(origin: String) {
        _state.update { it.copy(origin = origin) }
    }

    fun onDestinationChanged(destination: String) {
        _state.update { it.copy(destination = destination) }
    }

    fun search() {
        val currentState = _state.value
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val results = searchRidesUseCase(
                    currentState.origin,
                    currentState.destination,
                    Clock.System.now()
                )
                _state.update { it.copy(rides = results, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

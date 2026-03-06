package com.kevinroditi.cochiwawa.presentation.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.kevinroditi.cochiwawa.data.location.LocationProvider
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.data.socket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val userLocation: LatLng? = null,
    val driverLocation: LatLng? = null,
    val pickupPoint: LatLng? = null,
    val destinationPoint: LatLng? = null,
    val rideStarted: Boolean = false,
    val rideCompleted: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val api: AuthApi,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        observeWebSocketEvents()
    }

    private fun observeWebSocketEvents() {
        webSocketManager.events
            .onEach { json ->
                val type = json.optString("type")
                when (type) {
                    "DRIVER_LOCATION_UPDATED" -> {
                        val lat = json.optDouble("latitude")
                        val lng = json.optDouble("longitude")
                        _uiState.update { it.copy(driverLocation = LatLng(lat, lng)) }
                    }
                    "RIDE_STARTED" -> {
                        _uiState.update { it.copy(rideStarted = true) }
                    }
                    "RIDE_COMPLETED" -> {
                        _uiState.update { it.copy(rideCompleted = true) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun startLocationUpdates(driverId: String? = null) {
        locationProvider.getLocationUpdates()
            .onEach { location ->
                _uiState.update { it.copy(userLocation = LatLng(location.latitude, location.longitude)) }
                if (driverId != null) {
                    updateDriverLocation(driverId, location)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateDriverLocation(driverId: String, location: Location) {
        viewModelScope.launch {
            val query = """
                mutation {
                    updateDriverLocation(
                        driverId: "$driverId",
                        latitude: ${location.latitude},
                        longitude: ${location.longitude}
                    )
                }
            """.trimIndent()
            try {
                api.execute(GraphQLRequest(query))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

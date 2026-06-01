package com.kevinroditi.cochiwawa.presentation.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun RideMapScreen(
    driverLocation: LatLng? = null,
    passengerLocation: LatLng? = null,
    destination: LatLng? = null
) {
    val defaultPos = LatLng(-33.4489, -70.6693) // Santiago, Chile
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLocation ?: defaultPos, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        driverLocation?.let {
            Marker(
                state = rememberMarkerState(position = it),
                title = "Driver",
                snippet = "Current Location"
            )
        }

        passengerLocation?.let {
            Marker(
                state = rememberMarkerState(position = it),
                title = "Passenger",
                snippet = "Pickup Point"
            )
        }

        destination?.let {
            Marker(
                state = rememberMarkerState(position = it),
                title = "Destination"
            )
        }
    }
}

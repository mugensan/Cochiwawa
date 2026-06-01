package com.kevinroditi.cochiwawa.presentation.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun RideMapScreen(
    rideId: String,
    driverId: String?,
    isDriver: Boolean,
    viewModel: MapViewModel = hiltViewModel(),
    onRideCompleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startLocationUpdates(if (isDriver) driverId else null)
    }

    LaunchedEffect(uiState.rideCompleted) {
        if (uiState.rideCompleted) {
            onRideCompleted()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(uiState.userLocation ?: LatLng(0.0, 0.0), 15f)
        }

        // Auto-center camera when user location changes for the first time
        LaunchedEffect(uiState.userLocation) {
            uiState.userLocation?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(myLocationButtonEnabled = true),
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            uiState.driverLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Driver",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

            uiState.pickupPoint?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Pickup Point",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            }

            uiState.destinationPoint?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Drop-off Point",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }

            if (uiState.pickupPoint != null && uiState.destinationPoint != null) {
                Polyline(
                    points = listOf(uiState.pickupPoint!!, uiState.destinationPoint!!),
                    color = Color.Blue,
                    width = 5f
                )
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (uiState.rideStarted) "Ride in Progress" else "Waiting for Pickup",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isDriver && !uiState.rideStarted) {
                    Button(
                        onClick = { /* Logic to call startRide mutation */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Ride")
                    }
                } else if (isDriver && uiState.rideStarted) {
                    Button(
                        onClick = { /* Logic to call completeRide mutation */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Complete Ride")
                    }
                }
            }
        }
    }
}

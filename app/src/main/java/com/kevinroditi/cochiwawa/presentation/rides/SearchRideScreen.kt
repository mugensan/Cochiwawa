package com.kevinroditi.cochiwawa.presentation.rides

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevinroditi.cochiwawa.presentation.rides.components.RideCard

@Composable
fun SearchRideScreen(
    viewModel: SearchRideViewModel = hiltViewModel(),
    onRideSelected: (String) -> Unit
) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("1") }
    
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Find a Ride", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = origin,
            onValueChange = { origin = it },
            label = { Text("Origin") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = seats,
            onValueChange = { seats = it },
            label = { Text("Seats") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.searchRides(origin, destination, seats.toIntOrNull() ?: 1)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }
            LazyColumn {
                items(uiState.rides) { ride ->
                    RideCard(
                        ride = ride,
                        onBookClick = { onRideSelected(ride.id.toString()) }
                    )
                }
            }
        }
    }
}

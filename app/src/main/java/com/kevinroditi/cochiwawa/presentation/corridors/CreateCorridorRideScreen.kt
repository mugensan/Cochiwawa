package com.kevinroditi.cochiwawa.presentation.corridors

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateCorridorRideScreen(
    corridorId: String,
    onRideCreated: () -> Unit
) {
    var departureTime by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("3") }
    var price by remember { mutableStateOf("2.5") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Ride in Corridor", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = departureTime,
            onValueChange = { departureTime = it },
            label = { Text("Departure Time (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = seats,
            onValueChange = { seats = it },
            label = { Text("Available Seats") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price per Seat") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Logic to call CreateRide mutation with corridorId
                onRideCreated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Corridor Ride")
        }
    }
}

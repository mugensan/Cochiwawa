package com.kevinroditi.cochiwawa.presentation.corridors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.Ride
import com.kevinroditi.cochiwawa.presentation.rides.components.RideCard

@Composable
fun CorridorDetailScreen(
    corridorId: String,
    onRideSelected: (String) -> Unit
) {
    // Mock data for rides in this corridor
    val corridorName = "Maipú → Providencia"
    val rides = remember {
        listOf(
            Ride(id = 1, origin = "Maipú", destination = "Providencia", departureTime = "07:30", availableSeats = 2, pricePerSeat = 2.50),
            Ride(id = 2, origin = "Maipú", destination = "Providencia", departureTime = "07:45", availableSeats = 3, pricePerSeat = 2.50),
            Ride(id = 3, origin = "Maipú", destination = "Providencia", departureTime = "08:15", availableSeats = 1, pricePerSeat = 2.50)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = corridorName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select a departure time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(rides) { ride ->
                RideCard(
                    ride = ride,
                    requestedSeats = 1, // Default to 1
                    onBookClick = { onRideSelected(ride.id.toString()) }
                )
            }
        }
    }
}

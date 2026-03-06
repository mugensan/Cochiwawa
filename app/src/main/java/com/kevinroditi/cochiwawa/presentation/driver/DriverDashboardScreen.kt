package com.kevinroditi.cochiwawa.presentation.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cochiwawa.shared.Ride

@Composable
fun DriverDashboardScreen() {
    // In a real app, this would use a DriverViewModel
    val earnings = 1250.50
    val totalRides = 42
    val activeRides = listOf(
        Ride(id = 1, origin = "Central Park", destination = "JFK Airport", availableSeats = 2, pricePerSeat = 25.0, departureTime = "2023-10-27T10:00:00Z"),
        Ride(id = 2, origin = "Times Square", destination = "Brooklyn Bridge", availableSeats = 3, pricePerSeat = 15.0, departureTime = "2023-10-27T14:00:00Z")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Driver Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Earnings: $$earnings", style = MaterialTheme.typography.titleLarge)
                Text("Total Rides: $totalRides", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Active Rides", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(activeRides) { ride ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${ride.origin} -> ${ride.destination}", style = MaterialTheme.typography.bodyLarge)
                        Text("Seats available: ${ride.availableSeats}", style = MaterialTheme.typography.bodySmall)
                        Text("Price: $$${ride.pricePerSeat}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

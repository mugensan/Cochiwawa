package com.kevinroditi.cochiwawa.presentation.rides.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.Ride

@Composable
fun RideCard(
    ride: Ride,
    requestedSeats: Int,
    onBookClick: () -> Unit
) {
    val totalCost = ride.pricePerSeat * requestedSeats

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for driver photo
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) { }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(text = ride.driver?.fullName ?: "Unknown Driver", style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = "${ride.driver?.averageRating ?: 0.0}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${ride.pricePerSeat}/seat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Total: $$totalCost",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(text = "${ride.origin} → ${ride.destination}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Departure: ${ride.departureTime}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Available seats: ${ride.availableSeats}", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Ride")
            }
        }
    }
}

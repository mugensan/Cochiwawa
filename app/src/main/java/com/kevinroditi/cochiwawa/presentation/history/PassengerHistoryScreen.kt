package com.kevinroditi.cochiwawa.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.Booking

@Composable
fun PassengerHistoryScreen() {
    // This would ideally come from a ViewModel
    val bookings = remember {
        listOf<Booking>() // Fetch from repository
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Booking History", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No bookings found")
            }
        } else {
            LazyColumn {
                items(bookings) { booking ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${booking.ride?.origin} to ${booking.ride?.destination}", style = MaterialTheme.typography.titleMedium)
                            Text("Date: ${booking.createdAt}", style = MaterialTheme.typography.bodySmall)
                            Text("Seats: ${booking.seats}", style = MaterialTheme.typography.bodyMedium)
                            Text("Price: $$${(booking.ride?.pricePerSeat ?: 0.0) * booking.seats}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

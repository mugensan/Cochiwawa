package com.kevinroditi.cochiwawa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale

@Composable
fun RideScreen(client: GraphQLClient) {
    val scope = rememberCoroutineScope()
    var rides by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedRide by remember { mutableStateOf<Ride?>(null) }
    var showBookingDialog by remember { mutableStateOf(false) }

    val query = """
        query {
          allRides {
            nodes {
              id
              driverId
              origin
              destination
              departureTime
              availableSeats
              pricePerSeat
            }
          }
        }
    """.trimIndent()

    if (showBookingDialog && selectedRide != null) {
        BookingConfirmationDialog(
            ride = selectedRide!!,
            onConfirm = { _ ->
                scope.launch {
                    // Logic to book via GraphQL mutation would go here
                    showBookingDialog = false
                    selectedRide = null
                }
            },
            onDismiss = { showBookingDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val result = client.fetchGraphQL(query)
                    try {
                        val response = Json.decodeFromString<GraphQLResponse<AllRides>>(result)
                        rides = response.data?.allRides?.nodes ?: emptyList()
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh Rides")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(rides) { ride ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = {
                            selectedRide = ride
                            showBookingDialog = true
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${ride.origin} → ${ride.destination}", style = MaterialTheme.typography.titleMedium)
                            Text("Price: $${ride.pricePerSeat} | Available: ${ride.availableSeats}", style = MaterialTheme.typography.bodyMedium)
                            Text("Departs: ${ride.departureTime}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingConfirmationDialog(ride: Ride, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var seatsToBook by remember { mutableIntStateOf(1) }
    val total = ride.pricePerSeat * seatsToBook
    val fee = total * 0.08

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Booking") },
        text = {
            Column {
                Text("Ride: ${ride.origin} to ${ride.destination}")
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Seats: ")
                    Slider(
                        value = seatsToBook.toFloat(),
                        onValueChange = { seatsToBook = it.toInt() },
                        valueRange = 1f..ride.availableSeats.toFloat().coerceAtLeast(1f),
                        steps = (ride.availableSeats - 2).coerceAtLeast(0)
                    )
                    Text("$seatsToBook")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Fare: $${String.format(Locale.getDefault(), \"%.2f\", total)}")
                Text("Service Fee (8%): $${String.format(Locale.getDefault(), \"%.2f\", fee)}", style = MaterialTheme.typography.bodySmall)
                Text("Total to Pay: $${String.format(Locale.getDefault(), \"%.2f\", total + fee)}", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(seatsToBook) }) { Text("Pay & Book") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

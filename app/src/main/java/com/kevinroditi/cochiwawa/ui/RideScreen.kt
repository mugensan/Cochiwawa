package com.kevinroditi.cochiwawa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.GraphQLClient
import com.cochiwawa.shared.Ride
import com.cochiwawa.shared.AllRides
import com.cochiwawa.shared.GraphQLResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun RideScreen(client: GraphQLClient) {
    val scope = rememberCoroutineScope()
    var rides by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val result = client.fetchGraphQL(query)
                    try {
                        val response = Json.decodeFromString<GraphQLResponse<AllRides>>(result)
                        rides = response.data.allRides.nodes
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Rides")
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
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${ride.origin} → ${ride.destination}", style = MaterialTheme.typography.titleMedium)
                            Text("Seats: ${ride.availableSeats} | Price: $${ride.pricePerSeat}", style = MaterialTheme.typography.bodyMedium)
                            Text("Time: ${ride.departureTime}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

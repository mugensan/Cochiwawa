package com.kevinroditi.cochiwawa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun DriverScreen(client: GraphQLClient) {
    val scope = rememberCoroutineScope()
    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showCreateRideForm by remember { mutableStateOf(false) }

    val query = """
        query {
          allDrivers {
            nodes {
              id
              firstName
              lastName
              email
              phone
              vehicleModel
              vehiclePlate
            }
          }
        }
    """.trimIndent()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (showCreateRideForm) {
            CreateRideForm(
                onRideCreated = {
                    showCreateRideForm = false
                    // Optionally refresh lists
                },
                onCancel = { showCreateRideForm = false }
            )
        } else {
            Button(
                onClick = { showCreateRideForm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Create New Ride")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val result = client.fetchGraphQL(query)
                        try {
                            val response = Json.decodeFromString<GraphQLResponse<AllDrivers>>(result)
                            drivers = response.data?.allDrivers?.nodes ?: emptyList()
                        } catch (e: Exception) {
                            // Handle error
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Drivers List")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(drivers) { driver ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${driver.firstName} ${driver.lastName}", style = MaterialTheme.typography.titleMedium)
                                Text("Vehicle: ${driver.vehicleModel} (${driver.vehiclePlate})", style = MaterialTheme.typography.bodyMedium)
                                Text(driver.email, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRideForm(onRideCreated: () -> Unit, onCancel: () -> Unit) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var justification by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Create a Ride", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origin") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = seats, onValueChange = { seats = it }, label = { Text("Seats") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price per Seat") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = justification, 
            onValueChange = { justification = it }, 
            label = { Text("Price Justification (Fuel, etc.)") }, 
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Button(onClick = { onRideCreated() }) { Text("Post Ride") }
        }
    }
}

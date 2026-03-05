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
import com.cochiwawa.shared.Driver
import com.cochiwawa.shared.AllDrivers
import com.cochiwawa.shared.GraphQLResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun DriverScreen(client: GraphQLClient) {
    val scope = rememberCoroutineScope()
    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

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
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val result = client.fetchGraphQL(query)
                    try {
                        val response = Json.decodeFromString<GraphQLResponse<AllDrivers>>(result)
                        drivers = response.data.allDrivers.nodes
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Drivers")
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

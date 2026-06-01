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
import com.cochiwawa.shared.Passenger
import com.cochiwawa.shared.GraphQLResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Composable
fun PassengerScreen(client: GraphQLClient) {
    val scope = rememberCoroutineScope()
    var passengers by remember { mutableStateOf<List<Passenger>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val query = """
        query {
          allPassengers {
            nodes {
              id
              firstName
              lastName
              email
              phone
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
                        // In a real app, use a proper GraphQL library or better parsing
                        // For Phase 1, we'll manually handle the wrapper or use generic deserialization
                        // Assuming response structure matches GraphQLResponse
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Passengers")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(passengers) { passenger ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${passenger.firstName} ${passenger.lastName}", style = MaterialTheme.typography.titleMedium)
                            Text(passenger.email, style = MaterialTheme.typography.bodySmall)
                            Text(passenger.phone ?: "", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

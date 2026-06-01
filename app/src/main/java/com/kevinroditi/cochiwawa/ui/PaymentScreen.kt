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
import com.cochiwawa.shared.Payment
import com.cochiwawa.shared.AllPayments
import com.cochiwawa.shared.GraphQLResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun PaymentScreen(client: GraphQLClient) {
    val scope = rememberCoroutineScope()
    var payments by remember { mutableStateOf<List<Payment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val query = """
        query {
          allPayments {
            nodes {
              id
              rideId
              passengerId
              amount
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
                        val response = Json.decodeFromString<GraphQLResponse<AllPayments>>(result)
                        payments = response.data.allPayments.nodes
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Payments")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(payments) { payment ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Payment ID: ${payment.id}", style = MaterialTheme.typography.titleMedium)
                            Text("Amount: $${payment.amount}", style = MaterialTheme.typography.bodyMedium)
                            Text("Ride: ${payment.rideId} | Passenger: ${payment.passengerId}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

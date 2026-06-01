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
fun HistoryScreen(client: GraphQLClient) {
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
              status
            }
          }
        }
    """.trimIndent()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = client.fetchGraphQL(query)
            val response = Json.decodeFromString<GraphQLResponse<AllPayments>>(result)
            payments = response.data?.allPayments?.nodes ?: emptyList()
        } catch (e: Exception) {
            // Handle error
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Transaction History", style = MaterialTheme.typography.headlineMedium)
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
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Ride ID: ${payment.rideId}", style = MaterialTheme.typography.labelSmall)
                                Text("$${payment.amount}", style = MaterialTheme.typography.titleLarge)
                            }
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (payment.status == "SUCCESS") 
                                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = payment.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.kevinroditi.cochiwawa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FareCalculatorScreen(onFareCalculated: (Double) -> Unit) {
    var distance by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var driverCost by remember { mutableStateOf("") }
    var suggestedFare by remember { mutableStateOf(0.0) }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF8C00), Color(0xFFFF4500))
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fare Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distance (km)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = seats,
            onValueChange = { seats = it },
            label = { Text("Available Seats") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = driverCost,
            onValueChange = { driverCost = it },
            label = { Text("Extra Driver Costs (Fuel, etc.)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val dist = distance.toDoubleOrNull() ?: 0.0
                val st = seats.toIntOrNull() ?: 1
                val cost = driverCost.toDoubleOrNull() ?: 0.0
                
                // Formula: basePrice = distance * 0.5 + driverCost, totalFare = basePrice * seats
                val basePrice = dist * 0.5 + cost
                suggestedFare = basePrice / st // Suggested per seat
                onFareCalculated(suggestedFare)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(orangeGradient),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Calculate Suggested Fare", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (suggestedFare > 0.0) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Suggested Price per Seat:", fontSize = 18.sp)
                    Text("$${String.format("%.2f", suggestedFare)}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

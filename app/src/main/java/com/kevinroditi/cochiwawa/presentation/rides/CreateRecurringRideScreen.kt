package com.kevinroditi.cochiwawa.presentation.rides

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateRecurringRideScreen(
    onRideCreated: () -> Unit
) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("3") }
    var price by remember { mutableStateOf("5.0") }
    val daysOfWeek = remember { mutableStateListOf<String>() }
    val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Recurring Ride", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origin") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = departureTime, onValueChange = { departureTime = it }, label = { Text("Departure Time (HH:mm)") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Select Days:", modifier = Modifier.align(Alignment.Start))
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            allDays.forEach { day ->
                FilterChip(
                    selected = daysOfWeek.contains(day),
                    onClick = {
                        if (daysOfWeek.contains(day)) daysOfWeek.remove(day)
                        else daysOfWeek.add(day)
                    },
                    label = { Text(day) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        OutlinedTextField(value = seats, onValueChange = { seats = it }, label = { Text("Available Seats") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price per Seat") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Logic to call CreateRecurringRide mutation
                onRideCreated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Recurring Ride")
        }
    }
}

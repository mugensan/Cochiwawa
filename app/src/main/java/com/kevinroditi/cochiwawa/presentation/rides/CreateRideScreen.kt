package com.kevinroditi.cochiwawa.presentation.rides

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateRideScreen(
    onCreateRide: (origin: String, destination: String, seats: Int, price: Double, genderPref: String) -> Unit
) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("3") }
    var price by remember { mutableStateOf("10.0") }
    var genderPref by remember { mutableStateOf("ANY") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Offer a Ride", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origin") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = seats, onValueChange = { seats = it }, label = { Text("Available Seats") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price per Seat") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Text("Gender Preference:", modifier = Modifier.align(Alignment.Start))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = genderPref == "ANY", onClick = { genderPref = "ANY" })
            Text("Any")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = genderPref == "FEMALE_ONLY", onClick = { genderPref = "FEMALE_ONLY" })
            Text("Female Only")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onCreateRide(origin, destination, seats.toIntOrNull() ?: 1, price.toDoubleOrNull() ?: 0.0, genderPref)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Ride")
        }
    }
}

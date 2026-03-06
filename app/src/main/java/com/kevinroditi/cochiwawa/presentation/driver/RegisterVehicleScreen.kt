package com.kevinroditi.cochiwawa.presentation.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterVehicleScreen(
    onRegistrationSuccess: () -> Unit
) {
    var licenseNumber by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    
    var photoFrontUri by remember { mutableStateOf<Uri?>(null) }
    var photoBackUri by remember { mutableStateOf<Uri?>(null) }
    var photoLeftUri by remember { mutableStateOf<Uri?>(null) }
    var photoDriverSeatUri by remember { mutableStateOf<Uri?>(null) }
    var insuranceUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()

    val frontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoFrontUri = it }
    val backLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoBackUri = it }
    val leftLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoLeftUri = it }
    val seatLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoDriverSeatUri = it }
    val insuranceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { insuranceUri = it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Vehicle Registration", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = licenseNumber, onValueChange = { licenseNumber = it }, label = { Text("Driver License Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = plate, onValueChange = { plate = it }, label = { Text("Vehicle Plate") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Vehicle Model") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Vehicle Color") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Text("Upload Required Photos", style = MaterialTheme.typography.titleMedium)

        Button(onClick = { frontLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (photoFrontUri == null) "Front Photo" else "Front Photo Selected")
        }
        Button(onClick = { backLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (photoBackUri == null) "Back Photo (with Plate)" else "Back Photo Selected")
        }
        Button(onClick = { leftLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (photoLeftUri == null) "Side Photo" else "Side Photo Selected")
        }
        Button(onClick = { seatLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (photoDriverSeatUri == null) "Driver Seat Photo" else "Seat Photo Selected")
        }
        Button(onClick = { insuranceLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (insuranceUri == null) "Insurance Document" else "Insurance Selected")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Simplified: Logic to upload all images and then call registerVehicle mutation
                onRegistrationSuccess()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = licenseNumber.isNotBlank() && plate.isNotBlank() && photoFrontUri != null
        ) {
            Text("Submit for Verification")
        }
    }
}

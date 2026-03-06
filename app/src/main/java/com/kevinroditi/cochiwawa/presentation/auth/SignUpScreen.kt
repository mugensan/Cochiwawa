package com.kevinroditi.cochiwawa.presentation.auth

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateToSignIn: () -> Unit,
    onNavigateToVehicleRegistration: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("MALE") }
    var nationalId by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("PASSENGER") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        profilePhotoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nationalId,
            onValueChange = { nationalId = it },
            label = { Text("RUT / Passport / National ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Gender:", modifier = Modifier.align(Alignment.Start))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = gender == "MALE", onClick = { gender = "MALE" })
            Text("Male")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = gender == "FEMALE", onClick = { gender = "FEMALE" })
            Text("Female")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = gender == "OTHER", onClick = { gender = "OTHER" })
            Text("Other")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text("Role:", modifier = Modifier.align(Alignment.Start))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = role == "PASSENGER", onClick = { role = "PASSENGER" })
            Text("Passenger")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = role == "DRIVER", onClick = { role = "DRIVER" })
            Text("Driver")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { photoLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (profilePhotoUri == null) "Select Profile Photo" else "Photo Selected")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.error != null) {
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { 
                viewModel.signUp(
                    email, password, fullName, gender, nationalId, role, profilePhotoUri?.toString() ?: ""
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Sign Up")
            }
        }

        if (role == "DRIVER") {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onNavigateToVehicleRegistration, modifier = Modifier.fillMaxWidth()) {
                Text("Register Vehicle")
            }
        }

        TextButton(onClick = onNavigateToSignIn) {
            Text("Already have an account? Sign In")
        }
    }
}

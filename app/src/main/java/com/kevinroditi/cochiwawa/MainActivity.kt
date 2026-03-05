package com.kevinroditi.cochiwawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cochiwawa.shared.GraphQLClient
import com.kevinroditi.cochiwawa.ui.*
import com.kevinroditi.cochiwawa.ui.theme.CochiwawaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val client = GraphQLClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CochiwawaTheme {
                MainScreen(client)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(client: GraphQLClient) {
    var currentScreen by remember { mutableStateOf("Passengers") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cochiwawa Phase 1") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "Passengers",
                    onClick = { currentScreen = "Passengers" },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Passengers") }
                )
                NavigationBarItem(
                    selected = currentScreen == "Drivers",
                    onClick = { currentScreen = "Drivers" },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Drivers") }
                )
                NavigationBarItem(
                    selected = currentScreen == "Rides",
                    onClick = { currentScreen = "Rides" },
                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                    label = { Text("Rides") }
                )
                NavigationBarItem(
                    selected = currentScreen == "Payments",
                    onClick = { currentScreen = "Payments" },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Payments") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                "Passengers" -> PassengerScreen(client)
                "Drivers" -> DriverScreen(client)
                "Rides" -> RideScreen(client)
                "Payments" -> PaymentScreen(client)
            }
        }
    }
}

package com.kevinroditi.cochiwawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevinroditi.cochiwawa.data.socket.WebSocketManager
import com.kevinroditi.cochiwawa.presentation.auth.AuthViewModel
import com.kevinroditi.cochiwawa.presentation.auth.SignInScreen
import com.kevinroditi.cochiwawa.presentation.auth.SignUpScreen
import com.kevinroditi.cochiwawa.presentation.driver.DriverDashboardScreen
import com.kevinroditi.cochiwawa.presentation.driver.RegisterVehicleScreen
import com.kevinroditi.cochiwawa.presentation.history.PassengerHistoryScreen
import com.kevinroditi.cochiwawa.presentation.map.RideMapScreen
import com.kevinroditi.cochiwawa.presentation.rating.RatingScreen
import com.kevinroditi.cochiwawa.presentation.rides.CreateRideScreen
import com.kevinroditi.cochiwawa.presentation.rides.SearchRideScreen
import com.kevinroditi.cochiwawa.presentation.safety.EmergencyButton
import com.kevinroditi.cochiwawa.ui.theme.CochiwawaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var webSocketManager: WebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Connect to WebSocket on startup
        webSocketManager.connect()

        setContent {
            CochiwawaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val uiState by authViewModel.uiState.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (uiState.isAuthenticated && currentRoute != "signIn" && currentRoute != "signUp") {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == "marketplace",
                                    onClick = { navController.navigate("marketplace") },
                                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                                    label = { Text("Market") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "dashboard",
                                    onClick = { navController.navigate("dashboard") },
                                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                                    label = { Text("Driver") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "history",
                                    onClick = { navController.navigate("history") },
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                                    label = { Text("History") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "profile",
                                    onClick = { navController.navigate("profile") },
                                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    label = { Text("Profile") }
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (uiState.isAuthenticated) {
                            EmergencyButton()
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (uiState.isAuthenticated) "marketplace" else "signIn",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("signIn") {
                            SignInScreen(
                                viewModel = authViewModel,
                                onNavigateToSignUp = { navController.navigate("signUp") }
                            )
                        }
                        composable("signUp") {
                            SignUpScreen(
                                viewModel = authViewModel,
                                onNavigateToSignIn = { navController.navigate("signIn") },
                                onNavigateToVehicleRegistration = { navController.navigate("registerVehicle") }
                            )
                        }
                        composable("registerVehicle") {
                            RegisterVehicleScreen(
                                onRegistrationSuccess = { navController.navigate("dashboard") }
                            )
                        }
                        composable("marketplace") {
                            SearchRideScreen(
                                onRideSelected = { rideId ->
                                    navController.navigate("rideMap/$rideId")
                                }
                            )
                        }
                        composable("dashboard") {
                            DriverDashboardScreen()
                        }
                        composable("createRide") {
                            CreateRideScreen { origin, dest, seats, price, gender ->
                                // Logic to call CreateRide mutation
                                navController.navigate("dashboard")
                            }
                        }
                        composable(
                            route = "rideMap/{rideId}",
                            arguments = listOf(navArgument("rideId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rideId = backStackEntry.arguments?.getString("rideId") ?: ""
                            RideMapScreen(
                                rideId = rideId,
                                driverId = null, // Logic to determine if user is driver
                                isDriver = false,
                                onRideCompleted = {
                                    navController.navigate("rating/$rideId")
                                }
                            )
                        }
                        composable(
                            route = "rating/{rideId}",
                            arguments = listOf(navArgument("rideId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rideId = backStackEntry.arguments?.getString("rideId") ?: ""
                            RatingScreen(
                                rideId = rideId,
                                onRatingSubmitted = {
                                    navController.navigate("marketplace") {
                                        popUpTo("marketplace") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("history") {
                            PassengerHistoryScreen()
                        }
                        composable("profile") {
                            Button(onClick = { authViewModel.logout() }) {
                                Text("Logout")
                            }
                        }
                    }
                }

                LaunchedEffect(uiState.isAuthenticated) {
                    if (uiState.isAuthenticated) {
                        if (currentRoute == "signIn" || currentRoute == "signUp") {
                            navController.navigate("marketplace") {
                                popUpTo("signIn") { inclusive = true }
                            }
                        }
                    } else {
                        if (currentRoute != "signIn" && currentRoute != "signUp") {
                            navController.navigate("signIn") {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
    }
}

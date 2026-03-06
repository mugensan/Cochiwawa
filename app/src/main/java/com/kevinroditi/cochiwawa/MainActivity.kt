package com.kevinroditi.cochiwawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.kevinroditi.cochiwawa.presentation.chat.ChatScreen
import com.kevinroditi.cochiwawa.presentation.chat.ChatViewModel
import com.kevinroditi.cochiwawa.presentation.chat.TripChatsScreen
import com.kevinroditi.cochiwawa.presentation.corridors.CorridorDetailScreen
import com.kevinroditi.cochiwawa.presentation.corridors.CorridorListScreen
import com.kevinroditi.cochiwawa.presentation.corridors.CreateCorridorRideScreen
import com.kevinroditi.cochiwawa.presentation.driver.DriverDashboardScreen
import com.kevinroditi.cochiwawa.presentation.driver.RegisterVehicleScreen
import com.kevinroditi.cochiwawa.presentation.history.PassengerHistoryScreen
import com.kevinroditi.cochiwawa.presentation.map.RideMapScreen
import com.kevinroditi.cochiwawa.presentation.rating.RatingScreen
import com.kevinroditi.cochiwawa.presentation.rides.CreateRecurringRideScreen
import com.kevinroditi.cochiwawa.presentation.rides.CreateRideScreen
import com.kevinroditi.cochiwawa.presentation.rides.SearchRideScreen
import com.kevinroditi.cochiwawa.presentation.safety.EmergencyButton
import com.kevinroditi.cochiwawa.presentation.subscription.SubscriptionScreen
import com.kevinroditi.cochiwawa.ui.theme.CochiwawaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var webSocketManager: WebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        webSocketManager.connect()

        setContent {
            CochiwawaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val chatViewModel: ChatViewModel = hiltViewModel()
                
                val uiState by authViewModel.uiState.collectAsState()
                val chatUiState by chatViewModel.uiState.collectAsState()

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
                                    selected = currentRoute == "corridors",
                                    onClick = { navController.navigate("corridors") },
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    label = { Text("Corridors") }
                                )
                                // Conditionally show Chat tab
                                if (chatUiState.hasConfirmedBookings) {
                                    NavigationBarItem(
                                        selected = currentRoute == "tripChats" || currentRoute?.startsWith("chat/") == true,
                                        onClick = { navController.navigate("tripChats") },
                                        icon = { 
                                            BadgedBox(badge = { }) {
                                                Icon(Icons.Default.Email, contentDescription = "Chat")
                                            }
                                        },
                                        label = { Text("Chat") }
                                    )
                                }
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
                                },
                                onCorridorSelected = { corridorId ->
                                    navController.navigate("corridorDetail/$corridorId")
                                }
                            )
                        }
                        composable("corridors") {
                            CorridorListScreen(
                                onCorridorClick = { corridorId ->
                                    navController.navigate("corridorDetail/$corridorId")
                                }
                            )
                        }
                        composable(
                            route = "corridorDetail/{corridorId}",
                            arguments = listOf(navArgument("corridorId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val corridorId = backStackEntry.arguments?.getString("corridorId") ?: ""
                            CorridorDetailScreen(
                                corridorId = corridorId,
                                onRideSelected = { rideId ->
                                    navController.navigate("rideMap/$rideId")
                                }
                            )
                        }
                        composable("tripChats") {
                            TripChatsScreen(
                                viewModel = chatViewModel,
                                onChatRoomClick = { chatRoomId ->
                                    navController.navigate("chat/$chatRoomId")
                                }
                            )
                        }
                        composable(
                            route = "chat/{chatRoomId}",
                            arguments = listOf(navArgument("chatRoomId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: ""
                            ChatScreen(
                                chatRoomId = chatRoomId,
                                currentUserId = uiState.token ?: "unknown",
                                viewModel = chatViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("dashboard") {
                            DriverDashboardScreen()
                        }
                        composable("createRide") {
                            CreateRideScreen { origin, dest, seats, price, gender ->
                                navController.navigate("dashboard")
                            }
                        }
                        composable("createRecurringRide") {
                            CreateRecurringRideScreen {
                                navController.navigate("dashboard")
                            }
                        }
                        composable(
                            route = "createCorridorRide/{corridorId}",
                            arguments = listOf(navArgument("corridorId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val corridorId = backStackEntry.arguments?.getString("corridorId") ?: ""
                            CreateCorridorRideScreen(corridorId) {
                                navController.navigate("dashboard")
                            }
                        }
                        composable("subscriptions") {
                            SubscriptionScreen { plan ->
                                navController.navigate("profile")
                            }
                        }
                        composable(
                            route = "rideMap/{rideId}",
                            arguments = listOf(navArgument("rideId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rideId = backStackEntry.arguments?.getString("rideId") ?: ""
                            RideMapScreen(
                                rideId = rideId,
                                driverId = null,
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
                            Column {
                                Button(onClick = { navController.navigate("subscriptions") }, modifier = Modifier.padding(16.dp)) {
                                    Text("Commuter Pass")
                                }
                                Button(onClick = { authViewModel.logout() }, modifier = Modifier.padding(16.dp)) {
                                    Text("Logout")
                                }
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

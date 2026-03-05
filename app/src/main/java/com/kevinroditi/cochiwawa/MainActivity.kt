package com.kevinroditi.cochiwawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevinroditi.cochiwawa.domain.usecase.BookSeatUseCase
import com.kevinroditi.cochiwawa.domain.usecase.GetRideByIdUseCase
import com.kevinroditi.cochiwawa.domain.usecase.SearchRidesUseCase
import com.kevinroditi.cochiwawa.ui.confirmation.BookingConfirmationScreen
import com.kevinroditi.cochiwawa.ui.details.RideDetailsScreen
import com.kevinroditi.cochiwawa.ui.search.SearchScreen
import com.kevinroditi.cochiwawa.ui.theme.CochiwawaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var searchRidesUseCase: SearchRidesUseCase

    @Inject
    lateinit var getRideByIdUseCase: GetRideByIdUseCase

    @Inject
    lateinit var bookSeatUseCase: BookSeatUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CochiwawaTheme {
                CochiwawaNavHost(
                    searchRidesUseCase = searchRidesUseCase,
                    getRideByIdUseCase = getRideByIdUseCase,
                    bookSeatUseCase = bookSeatUseCase
                )
            }
        }
    }
}

@Composable
fun CochiwawaNavHost(
    searchRidesUseCase: SearchRidesUseCase,
    getRideByIdUseCase: GetRideByIdUseCase,
    bookSeatUseCase: BookSeatUseCase
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                searchRidesUseCase = searchRidesUseCase,
                onRideClick = { rideId ->
                    navController.navigate("details/$rideId")
                }
            )
        }
        composable(
            route = "details/{rideId}",
            arguments = listOf(navArgument("rideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rideId = backStackEntry.arguments?.getString("rideId") ?: return@composable
            RideDetailsScreen(
                rideId = rideId,
                getRideByIdUseCase = getRideByIdUseCase,
                bookSeatUseCase = bookSeatUseCase,
                onBack = { navController.popBackStack() },
                onBookingConfirmed = {
                    navController.navigate("confirmation")
                }
            )
        }
        composable("confirmation") {
            BookingConfirmationScreen(
                onFinish = {
                    navController.popBackStack("search", inclusive = false)
                }
            )
        }
    }
}

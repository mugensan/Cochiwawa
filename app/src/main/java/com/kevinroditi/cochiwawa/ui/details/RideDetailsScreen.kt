package com.kevinroditi.cochiwawa.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevinroditi.cochiwawa.domain.usecase.BookSeatUseCase
import com.kevinroditi.cochiwawa.domain.usecase.GetRideByIdUseCase
import com.kevinroditi.cochiwawa.presentation.details.RideDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailsScreen(
    rideId: String,
    getRideByIdUseCase: GetRideByIdUseCase,
    bookSeatUseCase: BookSeatUseCase,
    onBack: () -> Unit,
    onBookingConfirmed: () -> Unit
) {
    val viewModel = remember { RideDetailsViewModel(getRideByIdUseCase, bookSeatUseCase) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(rideId) {
        viewModel.loadRide(rideId)
    }

    LaunchedEffect(state.bookingSuccess) {
        if (state.bookingSuccess) {
            onBookingConfirmed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val ride = state.ride
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (ride != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "${ride.origin} → ${ride.destination}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PriceRow("Base Price", ride.basePrice.toEngineeringString(), ride.currency)
                        PriceRow("Gas Cost", ride.gasCost.toEngineeringString(), ride.currency)
                        PriceRow("Toll Cost", ride.tollCost.toEngineeringString(), ride.currency)
                        PriceRow("Maintenance", ride.maintenanceCost.toEngineeringString(), ride.currency)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        PriceRow(
                            label = "Service Protection Fee (8%)",
                            amount = ride.serviceFee.toEngineeringString(),
                            currency = ride.currency,
                            isHighlight = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total to Pay",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${ride.finalPrice.toEngineeringString()} ${ride.currency}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (ride.justificationNote != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Driver's Note",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ride.justificationNote!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = { viewModel.bookRide("passenger_123") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = ride.availableSeats > 0 && !state.isBooking
                ) {
                    if (state.isBooking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = if (ride.availableSeats > 0) "Pay & Book Seat" else "No Seats Left",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, amount: String, currency: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$amount $currency",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}

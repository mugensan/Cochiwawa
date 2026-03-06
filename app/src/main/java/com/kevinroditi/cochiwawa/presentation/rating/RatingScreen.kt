package com.kevinroditi.cochiwawa.presentation.rating

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingScreen(
    rideId: String,
    onRatingSubmitted: () -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Rate your Ride", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Row {
            for (i in 1..5) {
                IconButton(onClick = { rating = i }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i <= rating) Color(0xFFFFD700) else Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Call submitRating mutation
                onRatingSubmitted()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Rating")
        }
    }
}

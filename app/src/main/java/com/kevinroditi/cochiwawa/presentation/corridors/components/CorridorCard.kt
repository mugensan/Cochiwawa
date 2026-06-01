package com.kevinroditi.cochiwawa.presentation.corridors.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevinroditi.cochiwawa.domain.model.Corridor

@Composable
fun CorridorCard(
    corridor: Corridor,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = corridor.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Next Departure", style = MaterialTheme.typography.labelMedium)
                    Text(text = corridor.nextDeparture ?: "--:--", style = MaterialTheme.typography.bodyLarge)
                }
                Column {
                    Text(text = "Seats Left", style = MaterialTheme.typography.labelMedium)
                    Text(text = corridor.seatsLeft?.toString() ?: "0", style = MaterialTheme.typography.bodyLarge)
                }
                Column {
                    Text(text = "Price", style = MaterialTheme.typography.labelMedium)
                    Text(text = "$${corridor.pricePerSeat ?: 0.0}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

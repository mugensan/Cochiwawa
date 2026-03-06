package com.kevinroditi.cochiwawa.presentation.corridors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevinroditi.cochiwawa.domain.model.Corridor
import com.kevinroditi.cochiwawa.presentation.corridors.components.CorridorCard

@Composable
fun CorridorListScreen(
    onCorridorClick: (String) -> Unit
) {
    // Mock data for corridors
    val corridors = remember {
        listOf(
            Corridor("1", "Maipú → Providencia", "Maipú", "Providencia", 5, "07:30", 2, 2.50),
            Corridor("2", "Puente Alto → Las Condes", "Puente Alto", "Las Condes", 3, "08:00", 1, 3.00),
            Corridor("3", "Ñuñoa → Santiago Centro", "Ñuñoa", "Santiago Centro", 8, "07:45", 3, 1.80)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🔥 Popular routes today",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(corridors) { corridor ->
                CorridorCard(
                    corridor = corridor,
                    onClick = { onCorridorClick(corridor.id) }
                )
            }
        }
    }
}

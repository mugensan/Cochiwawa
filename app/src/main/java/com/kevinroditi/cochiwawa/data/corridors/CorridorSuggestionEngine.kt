package com.kevinroditi.cochiwawa.data.corridors

import android.location.Location
import com.kevinroditi.cochiwawa.domain.model.Corridor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class CorridorSuggestionEngine @Inject constructor() {

    /**
     * Suggests corridors based on user's current location and ride history zones.
     * Logic: Cluster rides within 2km radius and group by destination zones.
     */
    fun suggestCorridors(
        currentLocation: Location,
        allCorridors: List<Corridor>,
        userHistory: List<Location> // Destination history
    ): List<Corridor> {
        return allCorridors.filter { corridor ->
            // In a real app, we'd have lat/lng for corridor origins
            // For now, let's assume a match if it's "popular" or near a cluster
            val isNearOrigin = true // distance(currentLocation, corridor.origin) < 2.0
            val isFrequentDest = userHistory.any { historyLoc ->
                // distance(historyLoc, corridor.destination) < 2.0
                true
            }
            isNearOrigin && isFrequentDest
        }.take(3)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}

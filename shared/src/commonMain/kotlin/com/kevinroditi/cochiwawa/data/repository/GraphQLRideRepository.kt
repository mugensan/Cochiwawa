package com.kevinroditi.cochiwawa.data.repository

import com.cochiwawa.shared.GraphQLClient
import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.model.PriceBreakdown
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GraphQLRideNodes(
    val allRides: RideNodes
)

@Serializable
data class RideNodes(
    val nodes: List<GraphQLRide>
)

@Serializable
data class GraphQLRide(
    val id: Int,
    val origin: String,
    val destination: String,
    val availableSeats: Int,
    val price: Double
)

class GraphQLRideRepository(private val gqlClient: GraphQLClient) : RideRepository {
    override suspend fun searchRides(origin: String, destination: String, date: Instant): List<Ride> {
        val query = """
            query SearchRides(${"$"}origin: String!, ${"$"}destination: String!) {
              allRides(condition: {origin: ${"$"}origin, destination: ${"$"}destination}) {
                nodes {
                  id
                  origin
                  destination
                  availableSeats
                  price
                }
              }
            }
        """.trimIndent()
        
        return try {
            val response = gqlClient.query<GraphQLRideNodes>(query, mapOf("origin" to origin, "destination" to destination))
            response.data?.allRides?.nodes?.map { gqlRide ->
                Ride(
                    id = gqlRide.id.toString(),
                    driverId = "unknown", // Map as needed from your schema
                    driverName = "Backend Driver",
                    origin = gqlRide.origin,
                    destination = gqlRide.destination,
                    departureTime = date, // Placeholder
                    breakdown = PriceBreakdown(
                        basePrice = BigDecimal.fromDouble(gqlRide.price),
                        gasCost = BigDecimal.ZERO,
                        tollCost = BigDecimal.ZERO,
                        maintenanceCost = BigDecimal.ZERO,
                        justificationNote = ""
                    ),
                    availableSeats = gqlRide.availableSeats
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRideById(id: String): Ride? {
        // Implementation for single ride fetch
        return null
    }

    override suspend fun createRide(ride: Ride): Result<Unit> {
        // Implementation for creation mutation
        return Result.success(Unit)
    }

    override suspend fun updateAvailableSeats(rideId: String, newSeats: Int): Result<Unit> {
        // Implementation for update mutation
        return Result.success(Unit)
    }
}

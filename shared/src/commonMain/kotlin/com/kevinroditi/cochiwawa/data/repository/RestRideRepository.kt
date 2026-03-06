package com.kevinroditi.cochiwawa.data.repository

import com.cochiwawa.shared.NetworkClient
import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.model.PriceBreakdown
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiRide(
    val id: Int,
    val driver_id: Int,
    val origin: String,
    val destination: String,
    val departure_time: String,
    val available_seats: Int,
    val price_per_seat: String
)

class RestRideRepository(private val networkClient: NetworkClient) : RideRepository {
    override suspend fun searchRides(origin: String, destination: String, date: Instant): List<Ride> {
        return try {
            val apiRides: List<ApiRide> = networkClient.client.get("${networkClient.baseUrl}/rides") {
                parameter("origin", origin)
                parameter("destination", destination)
            }.body()

            apiRides.map { apiRide ->
                Ride(
                    id = apiRide.id.toString(),
                    driverId = apiRide.driver_id.toString(),
                    driverName = "Driver ${apiRide.driver_id}",
                    origin = apiRide.origin,
                    destination = apiRide.destination,
                    departureTime = Instant.parse(apiRide.departure_time.replace(" ", "T") + "Z"),
                    breakdown = PriceBreakdown(
                        basePrice = BigDecimal.fromDouble(apiRide.price_per_seat.toDouble()),
                        gasCost = BigDecimal.ZERO,
                        tollCost = BigDecimal.ZERO,
                        maintenanceCost = BigDecimal.ZERO,
                        justificationNote = null
                    ),
                    availableSeats = apiRide.available_seats
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRideById(id: String): Ride? {
        return try {
            val apiRide: ApiRide = networkClient.client.get("${networkClient.baseUrl}/rides/$id").body()
            Ride(
                id = apiRide.id.toString(),
                driverId = apiRide.driver_id.toString(),
                driverName = "Driver ${apiRide.driver_id}",
                origin = apiRide.origin,
                destination = apiRide.destination,
                departureTime = Instant.parse(apiRide.departure_time.replace(" ", "T") + "Z"),
                breakdown = PriceBreakdown(
                    basePrice = BigDecimal.fromDouble(apiRide.price_per_seat.toDouble()),
                    gasCost = BigDecimal.ZERO,
                    tollCost = BigDecimal.ZERO,
                    maintenanceCost = BigDecimal.ZERO,
                    justificationNote = null
                ),
                availableSeats = apiRide.available_seats
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createRide(ride: Ride): Result<Unit> {
        return Result.success(Unit) // TODO: Implement POST
    }

    override suspend fun updateAvailableSeats(rideId: String, newSeats: Int): Result<Unit> {
        return Result.success(Unit) // TODO: Implement PATCH
    }
}

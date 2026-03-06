package com.kevinroditi.cochiwawa.data.repository

import com.cochiwawa.shared.Ride
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.domain.model.Corridor
import com.kevinroditi.cochiwawa.domain.model.RecurringRide
import com.kevinroditi.cochiwawa.domain.model.Subscription
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class RideRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : RideRepository {

    private val gson = Gson()

    override suspend fun searchAvailableRides(origin: String, destination: String, seats: Int): Result<List<Ride>> {
        val query = """
            query {
                searchAvailableRides(origin: "$origin", destination: "$destination", seats: $seats) {
                    id origin destination departureTime availableSeats pricePerSeat
                    driver { fullName profilePhotoUrl averageRating }
                }
            }
        """.trimIndent()

        return try {
            val response = api.execute(GraphQLRequest(query))
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                val jsonArray = response.data?.getAsJsonArray("searchAvailableRides")
                val type = object : TypeToken<List<Ride>>() {}.type
                val rides: List<Ride> = gson.fromJson(jsonArray, type)
                Result.success(rides)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRide(origin: String, destination: String, seats: Int, price: Double, genderPref: String): Result<Boolean> {
        val query = """
            mutation {
                createRide(origin: "$origin", destination: "$destination", seats: $seats, price: $price, genderPref: "$genderPref")
            }
        """.trimIndent()
        return try {
            api.execute(GraphQLRequest(query))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRideDetails(rideId: String): Result<Ride> {
        val query = """
            query {
                getRideDetails(rideId: "$rideId") {
                    id origin destination departureTime availableSeats pricePerSeat
                    driver { fullName profilePhotoUrl averageRating }
                }
            }
        """.trimIndent()
        return try {
            val response = api.execute(GraphQLRequest(query))
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                val jsonObject = response.data?.getAsJsonObject("getRideDetails")
                val ride: Ride = gson.fromJson(jsonObject, Ride::class.java)
                Result.success(ride)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startRide(rideId: String): Result<Boolean> {
        val query = "mutation { startRide(rideId: \"$rideId\") }"
        return try { 
            api.execute(GraphQLRequest(query))
            Result.success(true) 
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun completeRide(rideId: String): Result<Boolean> {
        val query = "mutation { completeRide(rideId: \"$rideId\") }"
        return try { 
            api.execute(GraphQLRequest(query))
            Result.success(true) 
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun submitRating(rideId: String, rating: Int, comment: String?): Result<Boolean> {
        val query = """
            mutation {
                submitRating(rideId: "$rideId", rating: $rating, comment: "${comment ?: ""}")
            }
        """.trimIndent()
        return try {
            api.execute(GraphQLRequest(query))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCorridors(): Result<List<Corridor>> {
        // Mocking for now as per prompt instructions
        return Result.success(listOf(
            Corridor("1", "Maipú → Providencia", "Maipú", "Providencia", 5, "07:30", 2, 2.50),
            Corridor("2", "Puente Alto → Las Condes", "Puente Alto", "Las Condes", 3, "08:00", 1, 3.00),
            Corridor("3", "Ñuñoa → Santiago Centro", "Ñuñoa", "Santiago Centro", 8, "07:45", 3, 1.80)
        ))
    }

    override suspend fun createCorridorRide(corridorId: String, departureTime: String, seats: Int, price: Double): Result<Boolean> {
        val query = """
            mutation {
                createCorridorRide(corridorId: "$corridorId", departureTime: "$departureTime", seats: $seats, price: $price)
            }
        """.trimIndent()
        return try {
            api.execute(GraphQLRequest(query))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRecurringRide(recurringRide: RecurringRide): Result<Boolean> {
        val query = """
            mutation {
                createRecurringRide(
                    corridorId: "${recurringRide.corridorId}",
                    departureTime: "${recurringRide.departureTime}",
                    daysOfWeek: ${gson.toJson(recurringRide.daysOfWeek)},
                    seats: ${recurringRide.seats},
                    pricePerSeat: ${recurringRide.pricePerSeat}
                )
            }
        """.trimIndent()
        return try {
            api.execute(GraphQLRequest(query))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubscriptions(): Result<List<Subscription>> {
        return Result.success(emptyList())
    }

    override suspend fun subscribe(planType: String): Result<Boolean> {
        val query = "mutation { subscribe(planType: \"$planType\") }"
        return try {
            api.execute(GraphQLRequest(query))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

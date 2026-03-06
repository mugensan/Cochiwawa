package com.kevinroditi.cochiwawa.data.repository

import com.cochiwawa.shared.Ride
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
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
            val response = api.execute(GraphQLRequest(query))
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                Result.success(true)
            }
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
}

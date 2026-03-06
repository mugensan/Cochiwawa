package com.kevinroditi.cochiwawa.data.repository

import com.cochiwawa.shared.Booking
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.domain.repository.BookingRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : BookingRepository {

    private val gson = Gson()

    override suspend fun bookRide(rideId: Int, seats: Int): Result<Booking> {
        val query = """
            mutation {
                bookRide(rideId: $rideId, seats: $seats) {
                    id passengerId rideId seats createdAt
                    ride { id origin destination departureTime pricePerSeat }
                }
            }
        """.trimIndent()

        return try {
            val response = api.execute(GraphQLRequest(query))
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                val jsonObject = response.data?.getAsJsonObject("bookRide")
                val booking: Booking = gson.fromJson(jsonObject, Booking::class.java)
                Result.success(booking)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPassengerBookings(passengerId: String): Result<List<Booking>> {
        val query = """
            query {
                getPassengerBookings(passengerId: "$passengerId") {
                    id passengerId rideId seats createdAt
                    ride { id origin destination departureTime pricePerSeat }
                }
            }
        """.trimIndent()

        return try {
            val response = api.execute(GraphQLRequest(query))
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                val jsonArray = response.data?.getAsJsonArray("getPassengerBookings")
                val type = object : TypeToken<List<Booking>>() {}.type
                val bookings: List<Booking> = gson.fromJson(jsonArray, type)
                Result.success(bookings)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

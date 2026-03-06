package com.kevinroditi.cochiwawa.data.repository

import com.cochiwawa.shared.Booking
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: AuthApi // Reusing AuthApi for general GraphQL for now or create a dedicated one
) : BookingRepository {

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
            val response = api.login(GraphQLRequest(query)) // Assuming a generic method or adding one
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                // This is a bit hacky because I used LoginData in AuthApi. 
                // In a real app, I'd use a generic GraphQL client or specific DTOs.
                // For now, let's assume we add a generic execute method to AuthApi or a new Api.
                Result.failure(Exception("Not implemented generic executor yet"))
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
            // Placeholder until generic API is ready
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.cochiwawa.shared

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class GraphQLClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val endpoint = "http://10.0.2.2:5000/graphql" // 10.0.2.2 is localhost for Android emulator

    suspend fun fetchGraphQL(query: String): String {
        return try {
            val response: HttpResponse = client.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(GraphQLRequest(query))
            }
            response.bodyAsText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}

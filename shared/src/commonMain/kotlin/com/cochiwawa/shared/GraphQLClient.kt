package com.cochiwawa.shared

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class GraphQLClient {
    var authToken: String? = null

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // 10.0.2.2 is localhost for Android emulator.
    val endpoint = "http://10.0.2.2:5000/graphql"

    suspend inline fun <reified T> query(query: String, variables: Map<String, String>? = null): GraphQLResponse<T> {
        return client.post(endpoint) {
            contentType(ContentType.Application.Json)
            authToken?.let {
                header("Authorization", "Bearer $it")
            }
            setBody(GraphQLRequest(query, variables))
        }.body()
    }
}

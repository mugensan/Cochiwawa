package com.kevinroditi.cochiwawa.data.repository

import com.kevinroditi.cochiwawa.data.local.TokenStore
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.dto.AuthResponse
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AuthResponse> {
        val query = """
            mutation {
                signIn(email: "$email", password: "$password") {
                    token
                }
            }
        """.trimIndent()

        return try {
            val request = GraphQLRequest(query)
            val response = api.login(request)
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                val auth = response.data?.login ?: throw Exception("No data")
                tokenStore.saveToken(auth.token)
                Result.success(auth)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        gender: String,
        nationalId: String,
        role: String,
        profilePhotoUrl: String
    ): Result<AuthResponse> {
        val query = """
            mutation {
                signUp(
                    email: "$email",
                    password: "$password",
                    fullName: "$fullName",
                    gender: "$gender",
                    nationalId: "$nationalId",
                    role: "$role",
                    profilePhotoUrl: "$profilePhotoUrl"
                ) {
                    token
                }
            }
        """.trimIndent()

        return try {
            val request = GraphQLRequest(query)
            val response = api.register(request)
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                val auth = response.data?.register ?: throw Exception("No data")
                tokenStore.saveToken(auth.token)
                Result.success(auth)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenStore.clearToken()
    }

    override val authToken: Flow<String?> = tokenStore.authToken
}

package com.kevinroditi.cochiwawa.domain.repository

import com.kevinroditi.cochiwawa.data.remote.dto.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<AuthResponse>
    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        gender: String,
        nationalId: String,
        role: String,
        profilePhotoUrl: String
    ): Result<AuthResponse>
    suspend fun logout()
    val authToken: Flow<String?>
}

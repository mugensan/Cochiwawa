package com.kevinroditi.cochiwawa.domain.repository

import com.kevinroditi.cochiwawa.domain.model.User
import com.kevinroditi.cochiwawa.domain.model.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, name: String, password: String): Result<User>
    suspend fun signOut()
    suspend fun updateRole(role: UserRole): Result<Unit>
}

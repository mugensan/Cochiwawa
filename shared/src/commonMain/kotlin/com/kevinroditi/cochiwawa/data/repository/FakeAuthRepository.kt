package com.kevinroditi.cochiwawa.data.repository

import com.kevinroditi.cochiwawa.domain.model.User
import com.kevinroditi.cochiwawa.domain.model.UserRole
import com.kevinroditi.cochiwawa.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun signIn(email: String, password: String): Result<User> {
        val user = User("fake_id", "John Doe", UserRole.PASSENGER)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun signUp(email: String, name: String, password: String): Result<User> {
        val user = User("fake_id", name, UserRole.PASSENGER)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun updateRole(role: UserRole): Result<Unit> {
        _currentUser.value = _currentUser.value?.copy(role = role)
        return Result.success(Unit)
    }
}

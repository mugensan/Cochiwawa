package com.kevinroditi.cochiwawa.domain.usecase

import com.kevinroditi.cochiwawa.domain.model.User
import com.kevinroditi.cochiwawa.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, role: String): Result<User> {
        return repository.register(email, password, role)
    }
}

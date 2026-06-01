package com.kevinroditi.cochiwawa.presentation.auth

import com.kevinroditi.cochiwawa.domain.model.User

data class AuthState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRoleSelected: Boolean = false
)

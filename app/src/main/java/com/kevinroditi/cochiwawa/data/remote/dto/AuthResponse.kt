package com.kevinroditi.cochiwawa.data.remote.dto

data class AuthResponse(
    val token: String
)

data class LoginData(val login: AuthResponse)
data class RegisterData(val register: AuthResponse)

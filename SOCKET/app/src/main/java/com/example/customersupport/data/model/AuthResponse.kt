package com.example.customersupport.data.model

data class AuthResponse(
    val token: String,
    val role: String,
    val userId: String,
    val username: String
)

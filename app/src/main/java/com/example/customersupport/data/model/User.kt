package com.example.customersupport.data.model

data class User(
    val id: String,
    val username: String,
    val role: String // "CUSTOMER" or "MANAGER"
)

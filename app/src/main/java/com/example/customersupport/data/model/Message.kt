package com.example.customersupport.data.model

data class Message(
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false // Helper for UI
)

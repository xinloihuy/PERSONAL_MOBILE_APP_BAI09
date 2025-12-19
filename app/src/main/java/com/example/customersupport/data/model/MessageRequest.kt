package com.example.customersupport.data.model

data class MessageRequest(
    val senderId: String,
    val senderName: String,
    val recipientId: String? = null,
    val content: String,
    val role: String
)

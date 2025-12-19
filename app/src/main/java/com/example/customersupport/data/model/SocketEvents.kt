package com.example.customersupport.data.model

// Login Events
data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val role: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val error: String? = null
)

// Message Notification (Incoming)
data class MessageNotification(
    val id: Long,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val isFromCustomer: Boolean
)

// Message Response (Ack for sending)
data class SocketMessageResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

// Get Messages Response
data class MessagesResponse(
    val success: Boolean,
    val messages: List<MessageNotification>? = null,
    val error: String? = null
)

package com.example.customersupport.data.remote

import com.example.customersupport.data.model.LoginResponse
import com.example.customersupport.data.model.MessageNotification
import com.example.customersupport.data.model.MessagesResponse
import com.example.customersupport.data.model.SocketMessageResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume

class SocketChatRepository {
    
    // No longer need to hold a direct reference to the socket instance.
    // We will use the static methods of SocketManager for all interactions.

    suspend fun login(username: String, role: String): Result<LoginResponse> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val loginData = JSONObject().apply {
                    put("username", username)
                    put("password", "123456") // Use default password
                    put("role", role)
                }
                
                SocketManager.once("login_response") { args ->
                    try {
                        val response = args[0] as JSONObject
                        val loginResponse = LoginResponse(
                            success = response.getBoolean("success"),
                            token = response.optString("token"),
                            role = response.optString("role"),
                            userId = response.optString("username"), // Backend seems to send username as userId
                            username = response.optString("username"),
                            error = response.optString("error")
                        )
                        if (continuation.isActive) continuation.resume(Result.success(loginResponse))
                    } catch (e: Exception) {
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                }
                
                SocketManager.emit("login", loginData)
                
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
        }
    }
    
    suspend fun sendMessage(
        senderId: String,
        senderName: String,
        content: String,
        recipientId: String
    ): Result<SocketMessageResponse> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val messageData = JSONObject().apply {
                    put("senderId", senderId)
                    put("senderName", senderName)
                    put("recipientId", recipientId)
                    put("content", content)
                    put("role", if (recipientId == "manager") "CUSTOMER" else "MANAGER")
                }
                
                SocketManager.once("message_sent") { args -> // Listen for confirmation
                    try {
                        val response = args[0] as JSONObject
                        val messageResponse = SocketMessageResponse(
                            success = response.getBoolean("success"),
                            message = response.optString("message"),
                            error = response.optString("error")
                        )
                        if (continuation.isActive) continuation.resume(Result.success(messageResponse))
                    } catch (e: Exception) {
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                }
                
                SocketManager.emit("send_message", messageData)
                
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
        }
    }
    
    suspend fun getMessages(userId: String): Result<MessagesResponse> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val requestData = JSONObject().apply {
                    put("userId", userId)
                }
                
                SocketManager.once("messages_list") { args -> // Listen for messages_list
                    try {
                        val response = args[0] as JSONObject
                        val messagesArray = response.getJSONArray("messages")
                        val messagesList = mutableListOf<MessageNotification>()
                        
                        for (i in 0 until messagesArray.length()) {
                            val msgObj = messagesArray.getJSONObject(i)
                            messagesList.add(
                                MessageNotification(
                                    id = msgObj.getLong("id"),
                                    senderId = msgObj.getString("senderId"),
                                    senderName = msgObj.getString("senderUsername"),
                                    content = msgObj.getString("content"),
                                    timestamp = msgObj.getLong("timestamp"),
                                    isFromCustomer = msgObj.getString("senderRole") == "CUSTOMER"
                                )
                            )
                        }
                        
                        val messagesResponse = MessagesResponse(
                            success = true,
                            messages = messagesList
                        )
                        if (continuation.isActive) continuation.resume(Result.success(messagesResponse))

                    } catch (e: Exception) {
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                }
                
                SocketManager.emit("get_messages", requestData)
                
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
        }
    }
    
    fun listenForNewMessages(onNewMessage: (MessageNotification) -> Unit) {
        SocketManager.on("new_message") { args ->
            try {
                val messageObj = args[0] as JSONObject
                val message = MessageNotification(
                    id = messageObj.getLong("id"),
                    senderId = messageObj.getString("senderId"),
                    senderName = messageObj.getString("senderUsername"),
                    content = messageObj.getString("content"),
                    timestamp = messageObj.getLong("timestamp"),
                    isFromCustomer = messageObj.getString("senderRole") == "CUSTOMER"
                )
                onNewMessage(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun stopListeningForMessages() {
        SocketManager.off("new_message")
    }
}

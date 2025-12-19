package com.example.customersupport.data.remote

import com.example.customersupport.data.model.AuthResponse
import com.example.customersupport.data.model.LoginRequest
import com.example.customersupport.data.model.Message
import com.example.customersupport.data.model.MessageRequest
import com.example.customersupport.data.model.MessageResponse
import com.example.customersupport.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/chat/send")
    suspend fun sendMessage(@Body message: MessageRequest): Response<MessageResponse>

    // API lấy lịch sử tin nhắn
    @GET("api/chat/messages")
    suspend fun getMessages(@Query("userId") userId: String): Response<List<Message>>

    @GET("api/manager/customers")
    suspend fun getCustomers(): Response<List<User>>
}

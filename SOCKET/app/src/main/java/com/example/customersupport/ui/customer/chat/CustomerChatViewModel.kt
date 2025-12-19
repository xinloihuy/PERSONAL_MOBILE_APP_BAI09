package com.example.customersupport.ui.customer.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customersupport.data.local.SessionManager
import com.example.customersupport.data.model.Message
import com.example.customersupport.data.remote.SocketChatRepository
import kotlinx.coroutines.launch

class CustomerChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    private val repository = SocketChatRepository()

    init {
        repository.listenForNewMessages { notification ->
            Log.d("CustomerChatVM", "New message notification: ${notification.content}")
            val currentMessages = _messages.value.orEmpty().toMutableList()
            val message = Message(
                senderId = notification.senderId,
                senderName = notification.senderName,
                content = notification.content,
                timestamp = notification.timestamp,
                isFromMe = false // Assuming notifications are always from others
            )
            currentMessages.add(message)
            _messages.postValue(currentMessages)
        }
    }

    fun loadMessages(sessionManager: SessionManager) {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            repository.getMessages(userId).fold(
                onSuccess = { response ->
                    if (response.success && response.messages != null) {
                        val uiMessages = response.messages.map { msg ->
                            Message(
                                senderId = msg.senderId,
                                senderName = msg.senderName,
                                content = msg.content,
                                timestamp = msg.timestamp,
                                isFromMe = msg.senderId == userId
                            )
                        }
                        _messages.postValue(uiMessages)
                    }
                },
                onFailure = { Log.e("CustomerChatVM", "Failed to load messages", it) }
            )
        }
    }

    fun sendMessage(content: String, sessionManager: SessionManager) {
        val userId = sessionManager.getUserId() ?: return
        val username = sessionManager.getUsername() ?: "Customer"

        viewModelScope.launch {
            val myMessage = Message(
                senderId = userId,
                senderName = "Me",
                content = content,
                timestamp = System.currentTimeMillis(),
                isFromMe = true
            )
            
            val currentMessages = _messages.value.orEmpty().toMutableList()
            currentMessages.add(myMessage)
            _messages.postValue(currentMessages)
            
            repository.sendMessage(userId, username, content, "manager").fold(
                onSuccess = { Log.d("CustomerChatVM", "Message sent response: ${it.success}") },
                onFailure = { Log.e("CustomerChatVM", "Failed to send message", it) }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListeningForMessages()
    }
}

package com.example.customersupport.ui.manager.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customersupport.data.local.SessionManager
import com.example.customersupport.data.model.Message
import com.example.customersupport.data.remote.SocketChatRepository
import kotlinx.coroutines.launch

class ManagerChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    private val repository = SocketChatRepository()
    private var currentCustomerId: String? = null

    init {
        repository.listenForNewMessages { notification ->
            if (notification.senderId == currentCustomerId || !notification.isFromCustomer) {
                val message = Message(
                    senderId = notification.senderId,
                    senderName = notification.senderName,
                    content = notification.content,
                    timestamp = notification.timestamp,
                    isFromMe = !notification.isFromCustomer
                )
                val currentList = _messages.value.orEmpty().toMutableList()
                currentList.add(message)
                _messages.postValue(currentList)
            }
        }
    }

    fun loadMessages(customerId: String, sessionManager: SessionManager) {
        currentCustomerId = customerId
        viewModelScope.launch {
            repository.getMessages(customerId).fold(
                onSuccess = { response ->
                    if (response.success && response.messages != null) {
                        val managerId = sessionManager.getUserId()
                        val uiMessages = response.messages.map { msg ->
                            Message(
                                senderId = msg.senderId,
                                senderName = msg.senderName,
                                content = msg.content,
                                timestamp = msg.timestamp,
                                isFromMe = msg.senderId == managerId
                            )
                        }
                        _messages.postValue(uiMessages)
                    }
                },
                onFailure = { Log.e("ManagerChatVM", "Failed to load messages", it) }
            )
        }
    }

    fun sendMessage(content: String, sessionManager: SessionManager) {
        val customerId = currentCustomerId ?: return
        val managerId = sessionManager.getUserId() ?: return
        val managerName = sessionManager.getUsername() ?: "Manager"

        viewModelScope.launch {
            val myMessage = Message(
                senderId = managerId,
                senderName = "Me",
                content = content,
                timestamp = System.currentTimeMillis(),
                isFromMe = true
            )
            val currentList = _messages.value.orEmpty().toMutableList()
            currentList.add(myMessage)
            _messages.postValue(currentList)

            repository.sendMessage(managerId, managerName, content, customerId).fold(
                onSuccess = { Log.d("ManagerChatVM", "Message sent response: ${it.success}") },
                onFailure = { Log.e("ManagerChatVM", "Failed to send message", it) }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListeningForMessages()
    }
}

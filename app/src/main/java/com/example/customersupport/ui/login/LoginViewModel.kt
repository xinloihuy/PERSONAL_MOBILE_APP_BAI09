package com.example.customersupport.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customersupport.data.model.AuthResponse
import com.example.customersupport.data.remote.SocketChatRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    private val repository = SocketChatRepository()

    fun login(username: String, role: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Attempting login for $username")
            val result = repository.login(username, role)
            result.fold(
                onSuccess = { response ->
                    if (response.success) {
                        val auth = AuthResponse(
                            token = response.token ?: "",
                            role = response.role ?: "",
                            userId = response.userId ?: "",
                            username = response.username ?: ""
                        )
                        _loginResult.postValue(Result.success(auth))
                    } else {
                        _loginResult.postValue(Result.failure(Exception(response.error ?: "Unknown login error")))
                    }
                },
                onFailure = { e ->
                    _loginResult.postValue(Result.failure(e))
                }
            )
        }
    }
}

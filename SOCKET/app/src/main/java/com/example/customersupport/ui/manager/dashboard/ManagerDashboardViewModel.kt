package com.example.customersupport.ui.manager.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customersupport.data.model.User
import com.example.customersupport.data.remote.SocketManager
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ManagerDashboardViewModel : ViewModel() {

    private val _customers = MutableLiveData<MutableList<User>>(mutableListOf())
    val customers: LiveData<MutableList<User>> = _customers

    private val gson = Gson()

    init {
        listenForNewRequests()
    }

    fun loadCustomers() {
        // This API call is now redundant if we load customers via socket
        // However, we can keep it as an initial load
        // For now, let's assume we load customers from an event after login
    }

    private fun listenForNewRequests() {
        // Define a listener for the 'customer_list_update' event or similar
        val onCustomerListUpdate = Emitter.Listener { args ->
            try {
                val data = args[0] as JSONObject
                val customersArray = data.getJSONArray("customers")
                val newCustomerList = mutableListOf<User>()
                for (i in 0 until customersArray.length()) {
                    val customerObj = customersArray.getJSONObject(i)
                    val user = User(
                        id = customerObj.getString("id"),
                        username = customerObj.getString("username"),
                        role = customerObj.getString("role")
                    )
                    newCustomerList.add(user)
                }
                // Update LiveData on the main thread
                _customers.postValue(newCustomerList)

            } catch (e: Exception) {
                Log.e("ManagerDashboard", "Error parsing customer list", e)
            }
        }

        SocketManager.on("customer_list_update", onCustomerListUpdate)

        // Request initial customer list
        SocketManager.emit("get_customers")
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up the listener when ViewModel is destroyed
        SocketManager.off("customer_list_update")
    }
}

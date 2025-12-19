package com.example.customersupport.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.customersupport.data.local.SessionManager
import com.example.customersupport.data.remote.SocketManager
import com.example.customersupport.ui.customer.chat.CustomerChatActivity
import com.example.customersupport.ui.manager.dashboard.ManagerDashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val btnCustomer1 = findViewById<Button>(R.id.btnCustomer1)
        val btnCustomer2 = findViewById<Button>(R.id.btnCustomer2)
        val btnCustomer3 = findViewById<Button>(R.id.btnCustomer3)
        val btnManager = findViewById<Button>(R.id.btnManager)
        loading = findViewById(R.id.progressBar)

        SocketManager.connect()

        if (sessionManager.getAuthToken() != null && SocketManager.isConnected()) {
            navigateBasedOnRole(sessionManager.getUserRole())
        } else {
            sessionManager.clearSession()
        }

        btnCustomer1.setOnClickListener { performLogin("customer1", "CUSTOMER") }
        btnCustomer2.setOnClickListener { performLogin("customer2", "CUSTOMER") }
        btnCustomer3.setOnClickListener { performLogin("customer3", "CUSTOMER") }
        btnManager.setOnClickListener { performLogin("manager", "MANAGER") }

        viewModel.loginResult.observe(this) { result ->
            loading.visibility = View.GONE
            result.onSuccess { authResponse ->
                sessionManager.saveAuthToken(authResponse.token)
                sessionManager.saveUserRole(authResponse.role)
                sessionManager.saveUserDetails(authResponse.userId, authResponse.username)
                navigateBasedOnRole(authResponse.role)
            }.onFailure { error ->
                Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performLogin(username: String, role: String) {
        if (!SocketManager.isConnected()) {
            Toast.makeText(this, "Connecting... Please try again.", Toast.LENGTH_SHORT).show()
            SocketManager.connect()
            return
        }
        loading.visibility = View.VISIBLE
        viewModel.login(username, role)
    }

    private fun navigateBasedOnRole(role: String?) {
        val intent = when (role) {
            "CUSTOMER" -> Intent(this, CustomerChatActivity::class.java)
            "MANAGER" -> Intent(this, ManagerDashboardActivity::class.java)
            else -> null
        }
        intent?.let {
            startActivity(it)
            finish()
        } ?: run {
            loading.visibility = View.GONE
        }
    }
}

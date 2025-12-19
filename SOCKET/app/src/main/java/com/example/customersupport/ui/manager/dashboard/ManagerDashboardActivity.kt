package com.example.customersupport.ui.manager.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.customersupport.data.local.SessionManager
import com.example.customersupport.data.remote.SocketManager
import com.example.customersupport.ui.login.LoginActivity
import com.example.customersupport.ui.manager.chat.ManagerChatActivity

class ManagerDashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: ManagerDashboardViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_dashboard)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[ManagerDashboardViewModel::class.java]

        val rvCustomers = findViewById<RecyclerView>(R.id.rvCustomers)
        rvCustomers.layoutManager = LinearLayoutManager(this)
        
        adapter = CustomerAdapter(mutableListOf()) { user ->
            val intent = Intent(this, ManagerChatActivity::class.java)
            intent.putExtra("CUSTOMER_ID", user.id)
            intent.putExtra("CUSTOMER_NAME", user.username)
            startActivity(intent)
        }
        rvCustomers.adapter = adapter

        viewModel.customers.observe(this) { customers ->
            adapter.setCustomers(customers)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sessionManager.clearSession()
                SocketManager.disconnect()
                startActivity(Intent(this@ManagerDashboardActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
}

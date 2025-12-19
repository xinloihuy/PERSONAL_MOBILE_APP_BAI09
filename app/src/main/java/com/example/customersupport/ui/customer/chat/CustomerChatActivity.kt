package com.example.customersupport.ui.customer.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.customersupport.data.local.SessionManager
import com.example.customersupport.ui.common.adapter.MessageAdapter
import com.example.customersupport.ui.login.LoginActivity

class CustomerChatActivity : AppCompatActivity() {

    private lateinit var viewModel: CustomerChatViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_chat)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[CustomerChatViewModel::class.java]

        val rvMessages = findViewById<RecyclerView>(R.id.rvMessages)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        adapter = MessageAdapter(mutableListOf())
        rvMessages.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rvMessages.adapter = adapter

        viewModel.loadMessages(sessionManager)

        btnSend.setOnClickListener {
            val content = etMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.sendMessage(content, sessionManager)
                etMessage.text.clear()
            }
        }
        
        btnBack.setOnClickListener { 
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.messages.observe(this) { messages ->
            adapter.updateMessages(messages.toMutableList())
            rvMessages.scrollToPosition(adapter.itemCount - 1)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sessionManager.clearSession()
                // Disconnect when logging out
                com.example.customersupport.data.remote.SocketManager.disconnect()
                startActivity(Intent(this@CustomerChatActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
}

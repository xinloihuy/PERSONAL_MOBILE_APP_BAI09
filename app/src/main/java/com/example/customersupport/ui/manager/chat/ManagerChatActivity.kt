package com.example.customersupport.ui.manager.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.customersupport.data.local.SessionManager
import com.example.customersupport.ui.common.adapter.MessageAdapter

class ManagerChatActivity : AppCompatActivity() {

    private lateinit var viewModel: ManagerChatViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MessageAdapter
    private lateinit var etMessage: EditText
    private lateinit var rvMessages: RecyclerView
    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_chat)

        val customerId = intent.getStringExtra("CUSTOMER_ID") ?: return
        val customerName = intent.getStringExtra("CUSTOMER_NAME") ?: "Customer"

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[ManagerChatViewModel::class.java]

        rvMessages = findViewById(R.id.rvManagerMessages)
        etMessage = findViewById(R.id.etManagerMessage)
        tvTitle = findViewById(R.id.tvChatTitle)
        val btnSend = findViewById<Button>(R.id.btnManagerSend)
        val btnBack = findViewById<ImageButton>(R.id.btnManagerBack)

        tvTitle.text = "Chat with $customerName"

        // --- CORRECTED ADAPTER SETUP ---
        // 1. Create the adapter only ONCE.
        adapter = MessageAdapter(mutableListOf())
        rvMessages.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rvMessages.adapter = adapter

        viewModel.loadMessages(customerId, sessionManager)

        btnSend.setOnClickListener {
            val content = etMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.sendMessage(content, sessionManager)
                etMessage.text.clear()
            }
        }
        
        btnBack.setOnClickListener {
            finish() 
        }

        // 2. Observe changes and UPDATE the existing adapter's data.
        viewModel.messages.observe(this) { messages ->
            adapter.updateMessages(messages.toMutableList()) // Convert List to MutableList
            if (messages.isNotEmpty()) {
                rvMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}

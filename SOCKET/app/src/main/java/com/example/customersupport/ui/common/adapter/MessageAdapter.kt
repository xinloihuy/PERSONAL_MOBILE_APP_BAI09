package com.example.customersupport.ui.common.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.customersupport.data.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private var messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    fun updateMessages(newMessages: MutableList<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        private val container: LinearLayout = itemView as LinearLayout

        fun bind(message: Message) {
            tvSender.text = message.senderName
            tvContent.text = message.content

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvTimestamp.text = sdf.format(Date(message.timestamp))

            if (message.isFromMe) {
                container.gravity = Gravity.END
                tvContent.setBackgroundColor(0xFFDCF8C6.toInt())
                tvSender.visibility = View.GONE
            } else {
                container.gravity = Gravity.START
                tvContent.setBackgroundColor(0xFFFFFFFF.toInt())
                tvSender.visibility = View.VISIBLE
            }
        }
    }
}

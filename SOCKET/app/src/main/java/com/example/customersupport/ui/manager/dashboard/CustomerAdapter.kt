package com.example.customersupport.ui.manager.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.customersupport.data.model.User

class CustomerAdapter(
    private var customers: MutableList<User>,
    private val onCustomerClick: (User) -> Unit
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    fun setCustomers(newCustomers: List<User>) {
        customers.clear()
        customers.addAll(newCustomers)
        notifyDataSetChanged()
    }
    
    fun addCustomer(customer: User) {
        // Avoid duplicates
        if (customers.none { it.id == customer.id }) {
            customers.add(0, customer) // Add to top
            notifyItemInserted(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_row, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(customers[position])
    }

    override fun getItemCount(): Int = customers.size

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvId: TextView = itemView.findViewById(R.id.tvCustomerId)

        fun bind(user: User) {
            tvName.text = user.username
            tvId.text = "ID: ${user.id}"
            
            itemView.setOnClickListener {
                onCustomerClick(user)
            }
        }
    }
}

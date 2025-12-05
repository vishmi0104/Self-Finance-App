package com.example.moneymap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymap.R
import com.example.moneymap.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onDeleteClick: (Long?) -> Unit,
    private val onEditClick: (Transaction) -> Unit
) :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_transaction, parent, false) // Ensure you have 'item_transaction.xml'
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val tvTransactionCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        val btnEditTransaction: ImageButton = itemView.findViewById(R.id.btnEditTransaction)
        val btnDeleteTransaction: ImageButton = itemView.findViewById(R.id.btnDeleteTransaction)
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(transaction: Transaction) {
            tvTransactionTitle.text = transaction.title
            tvTransactionAmount.text = String.format(Locale.getDefault(), "%.2f", transaction.amount)
            tvTransactionCategory.text = transaction.category
            tvTransactionDate.text = dateFormatter.format(transaction.date)

            val color = if (transaction.type == "Expense") {
                ContextCompat.getColor(itemView.context, R.color.red)
            } else {
                ContextCompat.getColor(itemView.context, R.color.green)
            }
            tvTransactionAmount.setTextColor(color)

            btnEditTransaction.setOnClickListener {
                onEditClick(transaction)
            }

            btnDeleteTransaction.setOnClickListener {
                onDeleteClick(transaction.id)
            }
        }
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}


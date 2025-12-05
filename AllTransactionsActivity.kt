package com.example.moneymap

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymap.adapter.TransactionAdapter
import com.example.moneymap.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class AllTransactionsActivity : AppCompatActivity() {

    private lateinit var recyclerViewAllTransactions: RecyclerView
    private lateinit var allTransactionsAdapter: TransactionAdapter
    private val allTransactionsList = mutableListOf<Transaction>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var buttonBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alltransaction)

        buttonBack = findViewById(R.id.buttonBack)

        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerViewAllTransactions = findViewById(R.id.recyclerViewAllTransactions)
        recyclerViewAllTransactions.layoutManager = LinearLayoutManager(this)
        allTransactionsAdapter = TransactionAdapter(
            onDeleteClick = { transactionId ->
                transactionId?.let { deleteTransaction(it) }
            },
            onEditClick = { transaction ->
                val intent = Intent(this, AddTransactionActivity::class.java).apply {
                    putExtra("TRANSACTION_ID", transaction.id)
                    putExtra("TRANSACTION_TYPE", transaction.type)
                    putExtra("TRANSACTION_TITLE", transaction.title)
                    putExtra("TRANSACTION_AMOUNT", transaction.amount)
                    putExtra("TRANSACTION_CATEGORY", transaction.category)
                    putExtra("TRANSACTION_DATE", dateFormat.format(transaction.date))
                }
                startActivity(intent)
            }
        )
        recyclerViewAllTransactions.adapter = allTransactionsAdapter

        loadAllTransactions()
    }

    override fun onResume() {
        super.onResume()
        loadAllTransactions() // Reload the transactions when the activity resumes
    }

    private fun loadAllTransactions() {
        allTransactionsList.clear()
        val sharedPreferences = getSharedPreferences("transactions", MODE_PRIVATE)
        val allEntries = sharedPreferences.all.toList().sortedByDescending { it.first.toLongOrNull() ?: 0L }

        for ((key, value) in allEntries) {
            if (value is String) {
                val parts = value.split(",")
                if (parts.size == 5) {
                    try {
                        val id = key.toLongOrNull()
                        val type = parts[0]
                        val title = parts[1]
                        val amount = parts[2].toDoubleOrNull() ?: 0.0
                        val category = parts[3]
                        val date = dateFormat.parse(parts[4]) ?: Date()
                        allTransactionsList.add(Transaction(id, type, title, amount, category, date))
                    } catch (e: java.text.ParseException) {
                        Log.e("AllTransactionsActivity", "Error parsing date: $value", e)
                    }
                }
            }
        }
        allTransactionsAdapter.submitList(allTransactionsList.toList())
    }

    private fun deleteTransaction(transactionId: Long) {
        val sharedPreferences = getSharedPreferences("transactions", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(transactionId.toString())
        editor.apply()
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
        loadAllTransactions()
    }
}


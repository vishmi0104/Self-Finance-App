package com.example.moneymap

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymap.adapter.TransactionAdapter
import com.example.moneymap.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var textViewBalance: TextView
    private lateinit var textViewTotalIncome: TextView
    private lateinit var textViewTotalExpense: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var recyclerViewRecentTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val recentTransactionsList = mutableListOf<Transaction>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var tvSeeAllTransactions: TextView
    private lateinit var btnAddTransaction: Button
    private lateinit var btnSettings: Button // Removed btnViewSummary
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        textViewBalance = findViewById(R.id.textViewBalance)
        textViewTotalIncome = findViewById(R.id.textViewTotalIncome)
        textViewTotalExpense = findViewById(R.id.textViewTotalExpense)
        budgetProgressBar = findViewById(R.id.budgetProgress)
        recyclerViewRecentTransactions = findViewById(R.id.recyclerViewRecentTransactions)
        tvSeeAllTransactions = findViewById(R.id.tvSeeAllTransactions)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        btnSettings = findViewById(R.id.btnSettings) // Removed btnViewSummary
        sharedPreferences = getSharedPreferences("transactions", MODE_PRIVATE)

        // Set up RecyclerView for recent transactions
        recyclerViewRecentTransactions.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(
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
        recyclerViewRecentTransactions.adapter = transactionAdapter

        // Set click listener for "See All"
        tvSeeAllTransactions.setOnClickListener {
            val intent = Intent(this, AllTransactionsActivity::class.java)
            startActivity(intent)
        }

        // Button click listeners
        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        loadTransactions() // Load and display data
    }

    private fun deleteTransaction(transactionId: Long) {
        val editor = sharedPreferences.edit()
        editor.remove(transactionId.toString())
        editor.apply()
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
        loadTransactions() // Reload the list to reflect the deletion
        updateDashboard() // Update the dashboard after deletion
    }

    override fun onResume() {
        super.onResume()
        loadTransactions() // Reload data when the activity is resumed
    }

    private fun loadTransactions() {
        recentTransactionsList.clear()
        val allEntries = sharedPreferences.all
        val sortedEntries = allEntries.toList().sortedByDescending { it.first.toLongOrNull() ?: 0L }

        for ((key, value) in sortedEntries) {
            if (recentTransactionsList.size >= 5) break
            if (value is String) {
                val parts = value.split(",")
                if (parts.size == 5) {
                    try {
                        val id = key.toLongOrNull()
                        val type = parts[0]
                        val title = parts[1]
                        val amount = parts[2].toDouble()
                        val category = parts[3]
                        val date = dateFormat.parse(parts[4]) ?: Date()
                        recentTransactionsList.add(Transaction(id, type, title, amount, category, date))
                    } catch (e: NumberFormatException) {
                        android.util.Log.e("MainActivity", "Error parsing amount: $value", e)
                    } catch (e: java.text.ParseException) {
                        android.util.Log.e("MainActivity", "Error parsing date: $value", e)
                    }
                }
            }
        }
        transactionAdapter.submitList(recentTransactionsList.toList())
        updateDashboard()
    }

    private fun updateDashboard() {
        val allTransactions = loadAllTransactionsForCalculation()
        val income = allTransactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expense = allTransactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = income - expense

        val totalBudget = getSharedPreferences("budget_settings", MODE_PRIVATE)
            .getFloat("monthly_budget", 0f).toDouble()
        val budgetUsage = if (totalBudget > 0) (expense / totalBudget * 100).toInt() else 0

        textViewBalance.text = "Remaining Balance: LKR %.2f".format(balance)
        textViewTotalIncome.text = "Total Income: LKR %.2f".format(income)
        textViewTotalExpense.text = "Total Expense: LKR %.2f".format(expense)
        budgetProgressBar.progress = budgetUsage
    }

    private fun loadAllTransactionsForCalculation(): List<Transaction> {
        val allTransactions = mutableListOf<Transaction>()
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (value is String) {
                val parts = value.split(",")
                if (parts.size == 5) {
                    try {
                        val id = key.toLongOrNull()
                        val type = parts[0]
                        val title = parts[1]
                        val amount = parts[2].toDouble()
                        val category = parts[3]
                        val date = dateFormat.parse(parts[4]) ?: Date()
                        allTransactions.add(Transaction(id, type, title, amount, category, date))
                    } catch (e: NumberFormatException) {
                        android.util.Log.e("MainActivity", "Error parsing amount: $value", e)
                    } catch (e: java.text.ParseException) {
                        android.util.Log.e("MainActivity", "Error parsing date: $value", e)
                    }
                }
            }
        }
        return allTransactions
    }
}


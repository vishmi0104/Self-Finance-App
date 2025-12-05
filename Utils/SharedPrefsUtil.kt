package com.example.moneymap.util

import android.content.Context
import com.example.moneymap.model.Transaction
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson


object SharedPrefsUtil {

    private const val PREFS_NAME = "MoneyMapPrefs"
    private const val KEY_TRANSACTIONS = "transactions"

    // Save a single transaction to SharedPreferences
    fun saveTransaction(context: Context, transaction: Transaction) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing transactions (if any) and add the new one
        val existingTransactions = getAllTransactions(context).toMutableList()
        existingTransactions.add(transaction)

        // Save the updated list back to SharedPreferences
        val json = Gson().toJson(existingTransactions)
        editor.putString(KEY_TRANSACTIONS, json)
        editor.apply()
    }

    // Get all transactions from SharedPreferences
    fun getAllTransactions(context: Context): List<Transaction> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(KEY_TRANSACTIONS, "[]")

        // Parse the JSON string into a list of Transaction objects
        val transactionType = object : TypeToken<List<Transaction>>() {}.type
        return Gson().fromJson(json, transactionType)
    }

    // Clear all transactions from SharedPreferences (optional)
    fun clearTransactions(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_TRANSACTIONS)
        editor.apply()
    }
}
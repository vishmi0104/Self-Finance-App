package com.example.moneymap.model

import java.util.Date

data class Transaction(
    val id: Long? = null,
    val type: String? = null,
    val title: String,
    val amount: Double, // Change to Double
    val category: String,
    val date: Date
)

package com.example.moneymap.Utils

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
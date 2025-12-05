package com.example.moneymap.UtilS

import android.util.Patterns
import com.example.moneymap.Utils.ValidationResult

class InputValidator {

    fun isUsernameValid(username: String): ValidationResult {
        if (username.isEmpty()) {
            return ValidationResult(false, "Username cannot be empty")
        }
        if (username.length < 3) {
            return ValidationResult(false, "Username must be at least 3 characters")
        }
        if (username.length > 30) {
            return ValidationResult(false, "Username cannot exceed 30 characters")
        }
        if (!username.matches(Regex("^[a-zA-Z0-9._-]+$"))) {
            return ValidationResult(
                false,
                "Username can only contain letters, numbers, periods, underscores, and hyphens"
            )
        }
        return ValidationResult(true)
    }

    fun isEmailValid(email: String): ValidationResult {
        if (email.isEmpty()) {
            return ValidationResult(false, "Email cannot be empty")
        }
        if (!email.contains("@")) {
            return ValidationResult(false, "Email must contain the '@' symbol")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(false, "Invalid email format")
        }
        if (email.length > 100) {
            return ValidationResult(false, "Email cannot exceed 100 characters")
        }
        return ValidationResult(true)
    }

    fun isPasswordValid(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, "Password cannot be empty")
        }
        if (password.length < 8) {
            return ValidationResult(false, "Password must be at least 8 characters")
        }
        if (password.length > 50) {
            return ValidationResult(false, "Password cannot exceed 50 characters")
        }
        if (!password.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"))) {
            return ValidationResult(
                false,
                "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace"
            )
        }
        return ValidationResult(true)
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): ValidationResult {
        if (confirmPassword.isEmpty()) {
            return ValidationResult(false, "Confirm password cannot be empty")
        }
        return if (password == confirmPassword) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Passwords do not match")
        }
    }
}
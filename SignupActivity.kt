package com.example.moneymap.add_edit_transaction.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymap.MainActivity

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import com.example.moneymap.login.LoginActivity
import com.example.moneymap.R
import com.example.moneymap.UtilS.InputValidator


// Assuming you have a LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var signUpTitleTextView: TextView
    private lateinit var usernameTextInputLayout: TextInputLayout
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordTextInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var alreadyHaveAccountTextView: TextView
    private lateinit var loginTextView: TextView

    private val validator = InputValidator() // Create an instance of the InputValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI elements
        signUpTitleTextView = findViewById(R.id.signUpTitleTextView)
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordTextInputLayout = findViewById(R.id.confirmPasswordTextInputLayout)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        alreadyHaveAccountTextView = findViewById(R.id.alreadyHaveAccountTextView)
        loginTextView = findViewById(R.id.loginTextView)

        // Set up click listener for the Sign Up button
        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (validateInput(username, email, password, confirmPassword)) {
                // In a real application, you would typically send this data to a backend
                // for user registration and authentication.
                // For this simple example, we'll just show a success message and navigate.
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to the main screen (replace with your actual main activity)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Prevent going back to the sign-up page
            }
        }

        // Set up click listener for the Login link
        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close the sign-up page when navigating to login
        }
    }

    private fun validateInput(username: String, email: String, password: String, confirmPassword: String): Boolean {
        val isUsernameValidResult = validator.isUsernameValid(username)
        if (!isUsernameValidResult.isValid) {
            usernameTextInputLayout.error = isUsernameValidResult.errorMessage
            return false
        } else {
            usernameTextInputLayout.error = null
        }

        val isEmailValidResult = validator.isEmailValid(email)
        if (!isEmailValidResult.isValid) {
            emailTextInputLayout.error = isEmailValidResult.errorMessage
            return false
        } else {
            emailTextInputLayout.error = null
        }

        val isPasswordValidResult = validator.isPasswordValid(password)
        if (!isPasswordValidResult.isValid) {
            passwordTextInputLayout.error = isPasswordValidResult.errorMessage
            return false
        } else {
            passwordTextInputLayout.error = null
        }

        val doPasswordsMatchResult = validator.doPasswordsMatch(password, confirmPassword)
        if (!doPasswordsMatchResult.isValid) {
            confirmPasswordTextInputLayout.error = doPasswordsMatchResult.errorMessage
            return false
        } else {
            confirmPasswordTextInputLayout.error = null
        }

        return true
    }
}
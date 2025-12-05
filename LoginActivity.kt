package com.example.moneymap.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymap.MainActivity
import com.example.moneymap.R
import com.example.moneymap.UtilS.InputValidator
import com.example.moneymap.add_edit_transaction.auth.SignupActivity


// Import your InputValidator


class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewForgotPassword: TextView
    private lateinit var textViewSignUp: TextView
    private lateinit var textViewSignUp1: TextView
    private val validator = InputValidator() // Create an instance of InputValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Make sure this matches your login.xml file name

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
        textViewSignUp = findViewById(R.id.textViewSignUp)
        textViewSignUp1 = findViewById(R.id.textViewSignUp1)

        // Set click listeners
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Perform login logic here
            if (validateInput(email, password)) { // Use the validateInput function
                Toast.makeText(this, "Logging in with: $email", Toast.LENGTH_SHORT).show()
                // You would then navigate to the main activity
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Optional: close the login activity
            }
        }

        textViewForgotPassword.setOnClickListener {
            // Handle forgot password logic (e.g., navigate to a reset password screen)
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        textViewSignUp.setOnClickListener {
            // Handle sign up navigation
            Toast.makeText(this, "Don't have an account? clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupActivity::class.java))
        }

        textViewSignUp1.setOnClickListener {
            // Handle sign up navigation (same as the above TextView)
            Toast.makeText(this, "SignUp clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        val emailResult = validator.isEmailValid(email)
        val passwordResult = validator.isPasswordValid(password)

        if (!emailResult.isValid) {
            editTextEmail.error = emailResult.errorMessage
            return false
        } else {
            editTextEmail.error = null // Clear any previous error
        }

        if (!passwordResult.isValid) {
            editTextPassword.error = passwordResult.errorMessage
            return false
        } else {
            editTextPassword.error = null
        }

        return true
    }
}

package com.example.moneymap

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var editTextMonthlyBudget: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var saveButtonSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Initialize UI elements
        editTextMonthlyBudget = findViewById(R.id.editTextMonthlyBudget)
        sharedPreferences = getSharedPreferences("budget_settings", MODE_PRIVATE)
        saveButtonSettings = findViewById(R.id.saveButtonSettings)

        // Load saved budget
        val savedBudget = sharedPreferences.getFloat("monthly_budget", 0f)
        editTextMonthlyBudget.setText(if (savedBudget > 0) savedBudget.toString() else "")

        // Set click listener for the save button
        saveButtonSettings.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val budgetString = editTextMonthlyBudget.text.toString().trim()
        if (budgetString.isNotEmpty()) {
            try {
                val budget = budgetString.toFloat()
                if (budget >= 0) {
                    sharedPreferences.edit().putFloat("monthly_budget", budget).apply()
                    Toast.makeText(this, "Budget Saved", Toast.LENGTH_SHORT).show()
                    finish() // Optionally, close the activity after saving
                } else {
                    Toast.makeText(this, "Budget must be a non-negative number.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid budget format. Please enter a valid number.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter your monthly budget.", Toast.LENGTH_SHORT).show()
        }
    }
}

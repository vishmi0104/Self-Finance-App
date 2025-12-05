package com.example.moneymap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


import com.example.moneymap.R
import com.example.moneymap.add_edit_transaction.auth.SignupActivity


class SplashActivity : AppCompatActivity() {

    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startButton = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            // Decide where to navigate when "Start Here" is clicked
            // You might want to check if the user is already logged in.
            // For a new user flow, navigating to the sign-up page is common.
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close the splash screen so the user can't go back easily
        }

        // You might have a delay here for a traditional splash screen effect
        // If you only have the button, you might not need an initial delay.
        // If you want a short logo display before the button is fully interactive,
        // you can add a Handler like in the previous examples.
    }
}
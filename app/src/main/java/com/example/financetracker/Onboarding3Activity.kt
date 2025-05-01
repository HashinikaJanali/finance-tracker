package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button // Import Button
import androidx.appcompat.app.AppCompatActivity

class Onboarding3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding3)

        // Find the button in your layout
        val finishButton = findViewById<Button>(R.id.finishBtn)  // Replace with the actual ID of your button

        // Set a click listener for the button
        finishButton.setOnClickListener {
            // Create an intent to start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Finish Onboarding3Activity so the user can't go back to it by pressing back
            finish()
        }
    }
}

package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Onboarding1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboardingscreen1)

        // Find the Next button by its ID
        val btnNext = findViewById<Button>(R.id.nextBtn)

        // Set an OnClickListener for the Next button
        btnNext.setOnClickListener {
            // Create an Intent to navigate to Onboarding2Activity
            val intent = Intent(this, Onboarding2Activity::class.java)
            startActivity(intent)
        }
    }
}

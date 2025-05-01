package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // Hardcoded credentials for demo purposes
    private val correctUsername = "user"  // Changed from correctEmail
    private val correctPassword = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText) // Changed ID
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupText = findViewById<TextView>(R.id.signupText)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim() // Changed variable name
            val password = passwordEditText.text.toString().trim()

            when {
                username.isEmpty() -> {
                    Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                }
                username == correctUsername && password == correctPassword -> { // Changed condition
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Onboarding1Activity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show() // Changed message
                }
            }
        }

        signupText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}

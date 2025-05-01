package com.example.financetracker.model

import java.io.Serializable

data class Transaction(
    val category: String,       // Category of the transaction (Food, Transport, etc.)
    val amount: Double,         // Amount of the transaction
    val type: String,           // Type of the transaction (Income or Expense)
    val date: String,           // Date of the transaction
    val note: String = ""

// Optional note field, default is empty string
) : Serializable

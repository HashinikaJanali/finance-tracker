package com.example.financetracker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

class BudgetSettingsActivity : AppCompatActivity() {

    private lateinit var editTextBudget: EditText
    private lateinit var saveBudgetButton: Button
    private lateinit var currentBudgetTextView: TextView
    private lateinit var currentSpendingTextView: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var budgetProgressTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var monthlyBudget: Double = 0.00
    private val transactionList = mutableListOf<Transaction>()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        currency = Currency.getInstance("INR")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_settings)

        sharedPreferences = getSharedPreferences("FinanceTracker", MODE_PRIVATE)
        editTextBudget = findViewById(R.id.editTextBudget)
        saveBudgetButton = findViewById(R.id.saveBudgetButton)
        currentBudgetTextView = findViewById(R.id.currentBudgetTextView)
        currentSpendingTextView = findViewById(R.id.currentSpendingTextView)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        budgetProgressTextView = findViewById(R.id.budgetProgressTextView)

        loadBudget()
        loadTransactions() // Load transactions to calculate current spending
        updateBudgetDisplay()
        trackBudget()

        saveBudgetButton.setOnClickListener {
            val newBudget = editTextBudget.text.toString().toDoubleOrNull()
            if (newBudget != null && newBudget >= 0) {
                saveBudget(newBudget)
            } else {
                Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBudget() {
        monthlyBudget = sharedPreferences.getFloat("monthly_budget", 0.0f).toDouble()
    }

    private fun saveBudget(budget: Double) {
        monthlyBudget = budget
        sharedPreferences.edit().putFloat("monthly_budget", budget.toFloat()).apply()
        updateBudgetDisplay()
        trackBudget()
    }

    private fun updateBudgetDisplay() {
        currentBudgetTextView.text = "Current Budget: Rs " + currencyFormat.format(monthlyBudget).replace(currencyFormat.currency.symbol, "")
    }

    private fun loadTransactions() {
        transactionList.clear()
        val sharedPref = getSharedPreferences("FinanceTracker", MODE_PRIVATE)
        val json = sharedPref.getString("transaction_list", null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val savedList: MutableList<Transaction> = gson.fromJson(json, type)
            transactionList.addAll(savedList)
        } else {
            Log.d("BudgetSettingsActivity", "No transactions found in SharedPreferences")
        }
    }

    private fun trackBudget() {
        val currentMonthSpending = getCurrentMonthExpenses()
        currentSpendingTextView.text = "Current Spending (This Month): Rs " + currencyFormat.format(currentMonthSpending).replace(currencyFormat.currency.symbol, "")

        if (monthlyBudget > 0) {
            val progress = (currentMonthSpending / monthlyBudget * 100).toInt()
            budgetProgressBar.progress = progress
            budgetProgressTextView.text = "$progress% of Budget"

            val warningThreshold = 0.8 // 80% of the budget
            if (progress >= warningThreshold * 100) {
                Toast.makeText(this, "Warning: Nearing budget limit!", Toast.LENGTH_LONG).show()
            }
            if (progress >= 100) {
                Toast.makeText(this, "Alert: Budget exceeded!", Toast.LENGTH_LONG).show()
            }
        } else {
            budgetProgressBar.progress = 0
            budgetProgressTextView.text = "Budget not set"
        }
    }

    private fun getCurrentMonthExpenses(): Double {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        var monthlyExpenses = 0.0

        for (transaction in transactionList) {
            if (transaction.type.equals("Expense", ignoreCase = true)) {
                val transactionCalendar = Calendar.getInstance()
                val parts = transaction.date.split("/")
                if (parts.size == 3) {
                    try {
                        transactionCalendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                        transactionCalendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                        transactionCalendar.set(Calendar.YEAR, parts[2].toInt())

                        if (transactionCalendar.get(Calendar.MONTH) == currentMonth &&
                            transactionCalendar.get(Calendar.YEAR) == currentYear) {
                            monthlyExpenses += transaction.amount
                        }
                    } catch (e: NumberFormatException) {
                        Log.e("BudgetSettingsActivity", "Error parsing date: ${transaction.date}", e)
                    }
                }
            }
        }
        return monthlyExpenses
    }
}
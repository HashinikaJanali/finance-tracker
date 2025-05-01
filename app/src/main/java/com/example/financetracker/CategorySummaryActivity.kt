package com.example.financetracker

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financetracker.adapter.CategoryAdapter
import com.example.financetracker.databinding.CategorySummaryActivityBinding
import com.example.financetracker.model.CategorySpending
import com.example.financetracker.model.Transaction
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class CategorySummaryActivity : AppCompatActivity() {

    private lateinit var binding: CategorySummaryActivityBinding
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        currency = Currency.getInstance("INR")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CategorySummaryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load data when activity starts
        loadAndDisplayActualData()
    }

    private fun loadAndDisplayActualData() {
        val transactions = loadActualTransactions()

        if (transactions.isEmpty()) {
            showTrueEmptyState("No transactions available")
            return
        }

        val expenses = transactions.filter { it.type.equals("Expense", ignoreCase = true) }
        if (expenses.isEmpty()) {
            showTrueEmptyState("No expense transactions found")
            return
        }

        displayActualData(expenses)
    }

    private fun loadActualTransactions(): List<Transaction> {
        Log.d("CategorySummary", "loadActualTransactions() called") // ADDED
        return try {
            val sharedPrefs = getSharedPreferences("FinanceTracker", MODE_PRIVATE)
            val json = sharedPrefs.getString("transaction_list", null) // Load with "transaction_list"

            Log.d("CategorySummary", "JSON from SharedPreferences: $json") // ADDED

            if (json == null) {
                Log.d("CategorySummary", "JSON is null, returning empty list.") // ADDED
                return emptyList()
            }

            Gson().fromJson<List<Transaction>>(json, object : TypeToken<List<Transaction>>() {}.type)
                ?: emptyList<Transaction>().also {
                    Log.e("DataLoad", "Parsed transaction list is null")
                }
        } catch (e: Exception) {
            Log.e("DataLoad", "Error loading transactions", e)
            emptyList()
        }
    }

    private fun displayActualData(transactions: List<Transaction>) {
        Log.d("CategorySummary", "displayActualData() called with ${transactions.size} transactions.") // ADDED
        // Process and group data
        val categoryData = transactions
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.amount } }
            .toList()
            .sortedByDescending { (_, amount) -> amount }

        Log.d("CategorySummary", "Category data: $categoryData") // ADDED

        // Update RecyclerView
        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CategorySummaryActivity)
            adapter = CategoryAdapter(
                categoryData.map { (category, amount) ->
                    CategorySpending(
                        category = category,
                        amount = amount,
                        color = getCategoryColor(category)
                    )
                }
            )
        }

        // Update Pie Chart
        val pieEntries = categoryData.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        binding.pieChart.apply {
            data = PieData(PieDataSet(pieEntries, "").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 12f
                valueTextColor = Color.BLACK
            })
            animateY(1000)
            invalidate()
        }

        // Update Total
        val total = categoryData.sumOf { it.second }
        binding.totalAmountTextView.text = "Rs " + currencyFormat.format(total).replace(currencyFormat.currency.symbol, "")
    }

    private fun showTrueEmptyState(message: String) {
        binding.apply {
            pieChart.clear()
            pieChart.setNoDataText(message)
            binding.totalAmountTextView.text = "Rs " + currencyFormat.format(0.0).replace(currencyFormat.currency.symbol, "")
        }
        // No test data will be shown - only the empty state message
    }

    private fun getCategoryColor(category: String): Int {
        return when (category.lowercase()) {
            "food" -> ContextCompat.getColor(this, android.R.color.holo_red_light)
            "transport" -> ContextCompat.getColor(this, android.R.color.holo_blue_light)
            "bills" -> ContextCompat.getColor(this, android.R.color.holo_orange_light)
            "entertainment" -> ContextCompat.getColor(this, android.R.color.holo_purple)
            else -> ContextCompat.getColor(this, android.R.color.holo_green_light)
        }
    }
}
package com.example.financetracker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financetracker.adapter.TransactionAdapter
import com.example.financetracker.model.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList = mutableListOf<Transaction>()
    private lateinit var rvTransactions: RecyclerView
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var totalBalanceTextView: TextView
    private lateinit var incomeTextView: TextView
    private lateinit var expenseTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("FinanceTracker", MODE_PRIVATE)

        rvTransactions = findViewById(R.id.rvTransactions)
        fabAddTransaction = findViewById(R.id.fabAddTransaction)
        totalBalanceTextView = findViewById(R.id.totalBalanceTextView)
        incomeTextView = findViewById(R.id.incomeTextView)
        expenseTextView = findViewById(R.id.expenseTextView)

        transactionAdapter = TransactionAdapter(
            transactionList,
            { transaction, position ->
                val intent = Intent(this, EditTransactionActivity::class.java)
                intent.putExtra("transaction", transaction)
                intent.putExtra("position", position)
                startActivity(intent)
            },
            { position ->
                showDeleteConfirmationDialog(position)
            }
        )
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = transactionAdapter

        fabAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // Already on home
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionListActivity::class.java))
                    true
                }
                R.id.nav_analytics -> {
                    startActivity(Intent(this, CategorySummaryActivity::class.java))
                    true
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, BudgetSettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        transactionList.clear()
        val sharedPref = getSharedPreferences("FinanceTracker", MODE_PRIVATE) // Consistent SharedPreferences name
        val json = sharedPref.getString("transaction_list", null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val savedList: MutableList<Transaction> = gson.fromJson(json, type)
            transactionList.addAll(savedList)
        }
        transactionAdapter.notifyDataSetChanged()
        calculateAndDisplayTotals()
    }

    private fun saveTransactions() {
        val gson = Gson()
        val json = gson.toJson(transactionList)
        val sharedPref = getSharedPreferences("FinanceTracker", MODE_PRIVATE) // Consistent SharedPreferences name
        sharedPref.edit().putString("transaction_list", json).apply()
    }

    private fun deleteTransaction(position: Int) {
        transactionList.removeAt(position)
        saveTransactions()
        transactionAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
        calculateAndDisplayTotals()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                deleteTransaction(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun calculateAndDisplayTotals() {
        var totalIncome = 0.0
        var totalExpenses = 0.0

        for (transaction in transactionList) {
            if (transaction.type == "Income") {
                totalIncome += transaction.amount
            } else if (transaction.type == "Expense") {
                totalExpenses += transaction.amount
            }
        }

        val totalBalance = totalIncome - totalExpenses

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        currencyFormat.currency = Currency.getInstance("INR")

        val formattedBalance = "Rs " + currencyFormat.format(totalBalance).replace(currencyFormat.currency.symbol, "")
        val formattedIncome = "Rs " + currencyFormat.format(totalIncome).replace(currencyFormat.currency.symbol, "")
        val formattedExpense = "Rs " + currencyFormat.format(totalExpenses).replace(currencyFormat.currency.symbol, "")

        totalBalanceTextView.text = formattedBalance
        incomeTextView.text = formattedIncome
        expenseTextView.text = formattedExpense
    }
}
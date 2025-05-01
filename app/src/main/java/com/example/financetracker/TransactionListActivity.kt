package com.example.financetracker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financetracker.adapter.TransactionAdapter
import com.example.financetracker.databinding.ActivityTransactionListBinding
import com.example.financetracker.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionListBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val transactionList = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("FinanceTracker", MODE_PRIVATE) // Changed to "FinanceTracker"

        adapter = TransactionAdapter(
            transactionList,
            { transaction, position ->
                val intent = Intent(this@TransactionListActivity, EditTransactionActivity::class.java)
                intent.putExtra("transaction", transaction)
                intent.putExtra("position", position)
                startActivity(intent)
            },
            { position ->
                showDeleteConfirmationDialog(position)
            }
        )

        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTransactions.adapter = adapter

        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        val saved = sharedPreferences.getString("transaction_list", null)
        if (saved != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Transaction>>() {}.type
            val transactions: List<Transaction> = gson.fromJson(saved, type) ?: emptyList()
            transactionList.clear()
            transactionList.addAll(transactions)
            adapter.notifyDataSetChanged()
        } else {
            Log.d("TransactionListActivity", "No transactions found in SharedPreferences")
        }
    }

    private fun saveTransactions() {
        val gson = Gson()
        val json = gson.toJson(transactionList)
        sharedPreferences.edit().putString("transaction_list", json).apply()
    }

    private fun deleteTransaction(position: Int) {
        transactionList.removeAt(position)
        saveTransactions()
        adapter.notifyItemRemoved(position)
        Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
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
}
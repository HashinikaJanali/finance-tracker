package com.example.financetracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var transactionTypeGroup: RadioGroup
    private lateinit var categorySpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        amountEditText = findViewById(R.id.amountEditText)
        transactionTypeGroup = findViewById(R.id.transactionTypeGroup)
        categorySpinner = findViewById(R.id.categorySpinner)
        dateEditText = findViewById(R.id.dateEditText)
        noteEditText = findViewById(R.id.noteEditText)
        saveButton = findViewById(R.id.saveButton)

        val categories = arrayOf("Food", "Transport", "Bills", "Entertainment", "Health")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        dateEditText.setOnClickListener { showDatePickerDialog() }

        saveButton.setOnClickListener { saveTransaction() }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                dateEditText.setText("$day/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveTransaction() {
        val amountStr = amountEditText.text.toString()
        val selectedId = transactionTypeGroup.checkedRadioButtonId
        val type = findViewById<RadioButton>(selectedId)?.text.toString()
        val category = categorySpinner.selectedItem.toString()
        val date = dateEditText.text.toString()
        val note = noteEditText.text.toString()

        if (amountStr.isEmpty() || date.isEmpty() || selectedId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(category, amount, type, date, note)

        val sharedPref = getSharedPreferences("FinanceTracker", MODE_PRIVATE) // Changed to "FinanceTracker"
        val gson = Gson()
        val json = sharedPref.getString("transaction_list", null)
        val typeToken = object : TypeToken<MutableList<Transaction>>() {}.type
        val list = gson.fromJson<MutableList<Transaction>>(json, typeToken) ?: mutableListOf()

        list.add(transaction)

        sharedPref.edit().putString("transaction_list", gson.toJson(list)).apply()
        Toast.makeText(this, "Transaction Saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
package com.example.financetracker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText // Add if you have a title field
    private lateinit var amountEditText: EditText
    private lateinit var transactionTypeGroup: RadioGroup
    private lateinit var categorySpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button

    private var transactionToEdit: Transaction? = null
    private var transactionPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction) // Reuse the add transaction layout

        // Initialize views (add titleEditText if you have it in your layout)
        amountEditText = findViewById(R.id.amountEditText)
        transactionTypeGroup = findViewById(R.id.transactionTypeGroup)
        categorySpinner = findViewById(R.id.categorySpinner)
        dateEditText = findViewById(R.id.dateEditText)
        noteEditText = findViewById(R.id.noteEditText)
        saveButton = findViewById(R.id.saveButton)
        val addTitleTextView: TextView = findViewById(R.id.addTitle)
        addTitleTextView.text = "Edit Transaction"
        val saveButtonTextView: Button = findViewById(R.id.saveButton)
        saveButtonTextView.text = "Update Transaction"


        val categories = arrayOf("Food", "Transport", "Bills", "Entertainment", "Health")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        dateEditText.setOnClickListener { showDatePickerDialog() }
        saveButton.setOnClickListener { updateTransaction() }

        // Get the transaction to edit from the intent
        transactionToEdit = intent.getSerializableExtra("transaction") as? Transaction
        transactionPosition = intent.getIntExtra("position", -1)

        // Populate the fields with the transaction data
        transactionToEdit?.let { transaction ->
            amountEditText.setText(transaction.amount.toString())
            if (transaction.type == "Income") {
                findViewById<RadioButton>(R.id.incomeRadio).isChecked = true
            } else if (transaction.type == "Expense") {
                findViewById<RadioButton>(R.id.expenseRadio).isChecked = true
            }
            val categoryIndex = categories.indexOf(transaction.category)
            if (categoryIndex != -1) {
                categorySpinner.setSelection(categoryIndex)
            }
            dateEditText.setText(transaction.date)
            noteEditText.setText(transaction.note)
            // If you have a title field, set it here: titleEditText.setText(transaction.title)
        }
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

    private fun updateTransaction() {
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

        val updatedTransaction = Transaction(category, amount, type, date, note) // Include title if you have it

        val sharedPref = getSharedPreferences("transactions", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transaction_list", null)
        val typeToken = object : TypeToken<MutableList<Transaction>>() {}.type
        val transactionList = gson.fromJson<MutableList<Transaction>>(json, typeToken) ?: mutableListOf()

        if (transactionPosition != -1 && transactionPosition < transactionList.size) {
            transactionList[transactionPosition] = updatedTransaction
            sharedPref.edit().putString("transaction_list", gson.toJson(transactionList)).apply()
            Toast.makeText(this, "Transaction Updated!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error updating transaction", Toast.LENGTH_SHORT).show()
        }
    }
}
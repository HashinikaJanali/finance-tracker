package com.example.financetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TransactionItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_transaction)  // Referencing the transaction_item layout
    }
}

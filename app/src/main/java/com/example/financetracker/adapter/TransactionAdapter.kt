package com.example.financetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financetracker.databinding.ItemTransactionBinding
import com.example.financetracker.model.Transaction

class TransactionAdapter(
    private val transactionList: List<Transaction>,
    private val onEditClick: (Transaction, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.bind(transaction)

        holder.binding.buttonEdit.setOnClickListener {
            onEditClick(transaction, position)
        }

        holder.binding.buttonDelete.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.textViewCategory.text = transaction.category
            binding.textViewAmount.text = "${transaction.amount}"
            binding.textViewDate.text = transaction.date

        }
    }
}

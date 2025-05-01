package com.example.financetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financetracker.databinding.CategorySummaryItemBinding
import com.example.financetracker.model.CategorySpending
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class CategoryAdapter(
    private val categoryList: List<CategorySpending>,
    private val onItemClick: ((CategorySpending) -> Unit)? = null
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        currency = Currency.getInstance("INR")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategorySummaryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, currencyFormat) // Pass currencyFormat to ViewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categorySpending = categoryList[position]
        holder.bind(categorySpending)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(categorySpending)
        }
    }

    override fun getItemCount(): Int = categoryList.size

    class CategoryViewHolder(
        private val binding: CategorySummaryItemBinding,
        private val currencyFormat: NumberFormat // Receive currencyFormat
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categorySpending: CategorySpending) {
            binding.apply {
                textViewCategory.text = categorySpending.category
                textViewAmount.text = "Rs " + currencyFormat.format(categorySpending.amount).replace(currencyFormat.currency.symbol, "")
                root.setBackgroundColor(categorySpending.color)
            }
        }
    }
}
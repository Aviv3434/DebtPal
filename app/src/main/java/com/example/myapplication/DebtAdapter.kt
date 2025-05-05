package com.example.myapplication.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.DebtItem
import com.example.myapplication.databinding.ItemDebtBinding

class DebtAdapter(
    private val onDebtClick: (DebtItem) -> Unit
) : ListAdapter<DebtItem, DebtAdapter.DebtViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val binding = ItemDebtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DebtViewHolder(private val binding: ItemDebtBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DebtItem) {
            binding.tvName.text = "${item.payer} → ${item.receiver}"
            binding.tvAmount.text = "${item.amount} ₪"

            val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(item.date))
            binding.tvDate.text = formattedDate

            binding.root.setOnClickListener {
                onDebtClick(item)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<DebtItem>() {
        override fun areItemsTheSame(oldItem: DebtItem, newItem: DebtItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DebtItem, newItem: DebtItem) = oldItem == newItem
    }
}
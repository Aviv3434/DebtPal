package com.example.myapplication.ui.debtlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.DebtItem
import com.example.myapplication.databinding.ItemDebtBinding
import java.text.SimpleDateFormat
import java.util.*

class DebtAdapter(
    private val onItemClick: (DebtItem) -> Unit,
    private val onCheckboxClick: (DebtItem) -> Unit,
    private val onFavoriteClick: (DebtItem) -> Unit
) : ListAdapter<DebtItem, DebtAdapter.DebtViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val binding = ItemDebtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class DebtViewHolder(private val binding: ItemDebtBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(debt: DebtItem) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val directionText = "${debt.payer} → ${debt.receiver}"
            binding.tvName.text = directionText

            binding.tvAmount.text = "₪%.0f".format(debt.amount)

            val formattedDate = dateFormat.format(Date(debt.date))
            binding.tvDate.text = formattedDate

            binding.tvDescription.text = debt.description

            binding.checkboxHandled.isChecked = debt.isSettled
            binding.checkboxHandled.setOnCheckedChangeListener { _, isChecked ->
                // רק מעדכן isSettled
                onCheckboxClick(debt.copy(isSettled = isChecked, isFavorite = debt.isFavorite))
            }

            // favorite checkbox
            binding.checkboxFavorite.isChecked = debt.isFavorite
            binding.checkboxFavorite.setOnCheckedChangeListener { _, isFav ->
                // רק מעדכן isFavorite
                onFavoriteClick(debt.copy(isFavorite = isFav, isSettled = debt.isSettled))
            }

            binding.root.setOnClickListener {
                onItemClick(debt)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DebtItem>() {
        override fun areItemsTheSame(oldItem: DebtItem, newItem: DebtItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DebtItem, newItem: DebtItem): Boolean {
            return oldItem == newItem
        }
    }
}

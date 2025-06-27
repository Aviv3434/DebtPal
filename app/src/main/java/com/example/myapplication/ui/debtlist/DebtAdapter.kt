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
    private val onCheckboxClick: (DebtItem) -> Unit
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
            val context = binding.root.context
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // טקסט 'Tom → Ron'
            val directionText = "${debt.payer} → ${debt.receiver}"
            binding.tvName.text = directionText

            // סכום
            binding.tvAmount.text = "₪%.0f".format(debt.amount)

            // תאריך
            val formattedDate = dateFormat.format(Date(debt.date))
            binding.tvDate.text = formattedDate

            // תיאור
            binding.tvDescription.text = debt.description

            // מצב צ'קבוקס
            binding.checkboxHandled.isChecked = debt.isSettled

            // שינוי מצב הטופל
            binding.checkboxHandled.setOnCheckedChangeListener { _, isChecked ->
                val updated = debt.copy(isSettled = isChecked)
                onCheckboxClick(updated)
            }

            // קליק לפתיחת מסך עריכה
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

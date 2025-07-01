package com.example.myapplication.ui.debtlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.DebtItem
import com.example.myapplication.databinding.ItemDebtBinding
import java.text.SimpleDateFormat
import java.util.*

class DebtAdapter(
    private val onItemClick: (DebtItem) -> Unit,
    private val onCheckboxClick: (DebtItem) -> Unit,
    private val onFavoriteClick: (DebtItem) -> Unit
) : ListAdapter<DebtItem, DebtAdapter.DebtViewHolder>(DiffCallback()) {

    private var usdRate: Double? = null

    fun updateUsdRate(rate: Double) {
        usdRate = rate
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val binding = ItemDebtBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DebtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DebtViewHolder(
        private val binding: ItemDebtBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(debt: DebtItem) {
            val context = binding.root.context
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // 1) Load image with Glide or placeholder
            if (!debt.imageUri.isNullOrEmpty()) {
                Glide.with(context)
                    .load(debt.imageUri)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.ivDebtImage)
            } else {
                binding.ivDebtImage.setImageResource(R.drawable.image_placeholder)
            }

            binding.tvName.text = "${debt.payer} → ${debt.receiver}"
            binding.tvAmount.text = "₪%.0f".format(debt.amount)
            binding.tvDate.text = dateFormat.format(Date(debt.date))
            binding.tvDescription.text = debt.description

            usdRate?.let {
                val usd = debt.amount / it
                binding.tvAmountUsd.text = "≈ $%.2f".format(usd)
                binding.tvAmountUsd.visibility = View.VISIBLE
            } ?: run {
                binding.tvAmountUsd.visibility = View.GONE
            }

            binding.checkboxHandled.setOnCheckedChangeListener(null)
            binding.checkboxHandled.isChecked = debt.isSettled
            binding.checkboxHandled.setOnCheckedChangeListener { _, isChecked ->
                onCheckboxClick(debt.copy(isSettled = isChecked, isFavorite = debt.isFavorite))
            }

            binding.checkboxFavorite.setOnCheckedChangeListener(null)
            binding.checkboxFavorite.isChecked = debt.isFavorite
            binding.checkboxFavorite.setOnCheckedChangeListener { _, isFav ->
                onFavoriteClick(debt.copy(isFavorite = isFav, isSettled = debt.isSettled))
            }

            binding.root.setOnClickListener {
                onItemClick(debt)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DebtItem>() {
        override fun areItemsTheSame(old: DebtItem, new: DebtItem): Boolean =
            old.id == new.id

        override fun areContentsTheSame(old: DebtItem, new: DebtItem): Boolean =
            old == new
    }
}

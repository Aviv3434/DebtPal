package com.example.myapplication.ui.local

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.DebtItem
import com.example.myapplication.ui.debtlist.DebtAdapter
import kotlinx.coroutines.launch

class LocalCountFragment : Fragment(R.layout.fragment_local_count) {

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCount  = view.findViewById<TextView>(R.id.tvLocalCount)
        val rvPast   = view.findViewById<RecyclerView>(R.id.rvPastDebts)
        val tvNoPast = view.findViewById<TextView>(R.id.tvNoPast)

        val dao = AppDatabase.getDatabase(requireContext()).debtDao()

        // בונים את ה־Adapter עם 3 callbacks
        val adapter = DebtAdapter(
            onItemClick     = { _: DebtItem -> /* no-op */ },
            onCheckboxClick = { updatedDebt: DebtItem ->
                // רק isSettled
                lifecycleScope.launch { dao.updateDebt(updatedDebt) }
            },
            onFavoriteClick = { updatedDebt: DebtItem ->
                // רק isFavorite
                lifecycleScope.launch { dao.updateDebt(updatedDebt) }
            }
        )

        rvPast.layoutManager = LinearLayoutManager(requireContext())
        rvPast.adapter        = adapter

        // מאזינים לחובות שנסגרו
        dao.getSettledDebts().observe(viewLifecycleOwner) { settledList ->
            tvCount.text = getString(R.string.closed_debts, settledList.size)
            adapter.submitList(settledList)
            tvNoPast.visibility = if (settledList.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}

package com.example.myapplication.ui.local

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.DebtItem
import com.example.myapplication.ui.debtlist.DebtAdapter
import com.example.myapplication.viewmodel.DebtViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocalCountFragment : Fragment(R.layout.fragment_local_count) {

    private val debtViewModel: DebtViewModel by viewModels()

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCount  = view.findViewById<TextView>(R.id.tvLocalCount)
        val rvPast   = view.findViewById<RecyclerView>(R.id.rvPastDebts)
        val tvNoPast = view.findViewById<TextView>(R.id.tvNoPast)

        val adapter = DebtAdapter(
            onItemClick     = { _: DebtItem ->  },
            onCheckboxClick = { updatedDebt ->
                debtViewModel.updateDebt(updatedDebt)
            },
            onFavoriteClick = { updatedDebt ->
                debtViewModel.updateDebt(updatedDebt)
            }
        )

        rvPast.layoutManager = LinearLayoutManager(requireContext())
        rvPast.adapter        = adapter


        debtViewModel.settledDebts.observe(viewLifecycleOwner) { settledList ->
            tvCount.text = getString(R.string.closed_debts, settledList.size)
            adapter.submitList(settledList)
            tvNoPast.visibility = if (settledList.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}

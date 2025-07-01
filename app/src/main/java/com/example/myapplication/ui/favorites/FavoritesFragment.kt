package com.example.myapplication.ui.favorites

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
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val debtViewModel: DebtViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvFavorites = view.findViewById<RecyclerView>(R.id.rvFavorites)
        val tvEmpty     = view.findViewById<TextView>(R.id.tvEmpty)

        val adapter = DebtAdapter(
            onItemClick     = { /* no-op or navigate */ },
            onCheckboxClick = { updatedDebt: DebtItem ->
                debtViewModel.updateDebt(updatedDebt)
            },
            onFavoriteClick = { updatedDebt: DebtItem ->
                debtViewModel.updateDebt(updatedDebt)
            }
        )

        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.adapter        = adapter


        debtViewModel.favoriteDebts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}

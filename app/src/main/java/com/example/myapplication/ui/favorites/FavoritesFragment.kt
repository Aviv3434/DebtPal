package com.example.myapplication.ui.favorites

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

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // References
        val rvFavorites = view.findViewById<RecyclerView>(R.id.rvFavorites)
        val tvEmpty     = view.findViewById<TextView>(R.id.tvEmpty)
        val dao         = AppDatabase.getDatabase(requireContext()).debtDao()

        // Adapter with three callbacks
        val adapter = DebtAdapter(
            onItemClick     = { /* no-op or navigate if desired */ },
            onCheckboxClick = { updatedDebt: DebtItem ->
                // update only isSettled
                lifecycleScope.launch {
                    dao.updateDebt(updatedDebt)
                }
            },
            onFavoriteClick = { updatedDebt: DebtItem ->
                // update only isFavorite
                lifecycleScope.launch {
                    dao.updateDebt(updatedDebt)
                }
            }
        )

        // RecyclerView setup
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.adapter        = adapter

        // Observe favorites
        dao.getFavoriteDebts().observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}

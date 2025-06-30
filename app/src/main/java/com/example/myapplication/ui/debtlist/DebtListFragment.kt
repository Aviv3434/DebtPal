package com.example.myapplication.ui.debtlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentDebtListBinding
import com.example.myapplication.viewmodel.DebtViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebtListFragment : Fragment() {

    private var _binding: FragmentDebtListBinding? = null
    private val binding get() = _binding!!

    private val debtViewModel: DebtViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebtListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DebtAdapter(
            onItemClick = { selectedDebt ->
                val action = DebtListFragmentDirections
                    .actionDebtListFragmentToAddEditDebtFragment(selectedDebt)
                findNavController().navigate(action)
            },
            onCheckboxClick = { updatedDebt ->
                debtViewModel.updateDebt(updatedDebt)
            },
            onFavoriteClick = { updatedDebt ->
                debtViewModel.updateDebt(updatedDebt)
            }
        )

        binding.recyclerDebts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDebts.adapter = adapter

        // מאזינים לחובות פתוחים בלבד
        debtViewModel.unsettledDebts.observe(viewLifecycleOwner) { openDebts ->
            adapter.submitList(openDebts)
            binding.tvEmptyList.visibility =
                if (openDebts.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.databinding.FragmentStatisticsBinding
import com.example.myapplication.repository.DebtRepository
import com.example.myapplication.viewmodel.DebtViewModel
import com.example.myapplication.viewmodel.DebtViewModelFactory
import java.text.DecimalFormat

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var debtViewModel: DebtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getDatabase(requireContext()).debtDao()
        val repository = DebtRepository(dao)
        val factory = DebtViewModelFactory(repository)
        debtViewModel = ViewModelProvider(this, factory)[DebtViewModel::class.java]

        observeStatistics()
    }

    private fun observeStatistics() {
        debtViewModel.allDebts.observe(viewLifecycleOwner) { debts ->
            val totalAmount = debts.sumOf { it.amount }
            val settledAmount = debts.filter { it.isSettled }.sumOf { it.amount }
            val unsettledAmount = totalAmount - settledAmount

            val formatter = DecimalFormat("#,##0.##")

            binding.tvTotalDebts.text = "Total debts: ${debts.size}"
            binding.tvTotalAmount.text = "Total amount: ${formatter.format(totalAmount)} ₪"
            binding.tvSettledAmount.text = "Settled debts: ${formatter.format(settledAmount)} ₪"
            binding.tvOwedAmount.text = "Open debts: ${formatter.format(unsettledAmount)} ₪"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

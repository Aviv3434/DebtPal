package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.databinding.FragmentDebtListBinding
import com.example.myapplication.repository.DebtRepository
import com.example.myapplication.viewmodel.DebtViewModel
import com.example.myapplication.viewmodel.DebtViewModelFactory
import com.example.myapplication.data.DebtAdapter


class DebtListFragment : Fragment() {

    private var _binding: FragmentDebtListBinding? = null
    private val binding get() = _binding!!

    private lateinit var debtViewModel: DebtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebtListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // יצירת ViewModel עם Factory
        val dao = AppDatabase.getDatabase(requireContext()).debtDao()
        val repository = DebtRepository(dao)
        val factory = DebtViewModelFactory(repository)
        debtViewModel = ViewModelProvider(this, factory)[DebtViewModel::class.java]

        val adapter = DebtAdapter { selectedDebt ->
            val action = DebtListFragmentDirections
                .actionDebtListFragmentToAddEditDebtFragment(selectedDebt)
            findNavController().navigate(action)
        }

        binding.recyclerDebts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDebts.adapter = adapter

        debtViewModel.allDebts.observe(viewLifecycleOwner) { debtList ->
            val openDebts = debtList.filter { !it.isSettled }
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

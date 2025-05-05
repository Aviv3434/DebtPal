package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddDebt.setOnClickListener {
            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToAddEditDebtFragment(null)
            findNavController().navigate(action)
        }

        binding.btnEditDebt.setOnClickListener {
            findNavController().navigate(
                MainMenuFragmentDirections.actionMainMenuFragmentToDebtListFragment()
            )
        }

        binding.btnStatistics.setOnClickListener {
            findNavController().navigate(
                MainMenuFragmentDirections.actionMainMenuFragmentToStatisticsFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

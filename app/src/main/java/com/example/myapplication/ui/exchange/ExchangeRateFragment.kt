package com.example.myapplication.ui.exchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.databinding.FragmentExchangeRateBinding
import com.example.myapplication.viewmodel.ExchangeRateViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExchangeRateFragment : Fragment() {

    private val viewModel: ExchangeRateViewModel by viewModels()
    private var _binding: FragmentExchangeRateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getRates("USD")

        viewModel.exchangeRates.observe(viewLifecycleOwner) { response ->
            response?.let {
                val rates = it.rates
                val text = rates.entries.joinToString("\n") { entry -> "${entry.key}: ${entry.value}" }
                binding.tvRates.text = text
            } ?: run {
                binding.tvRates.text = "Failed to fetch exchange rates."
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

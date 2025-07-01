package com.example.myapplication.ui.exchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

        viewModel.getRates()

        viewModel.usdRate.observe(viewLifecycleOwner) { usdRate ->
            if (usdRate != null) {
                binding.tvRates.text = "USD: $usdRate"
            } else {
                binding.tvRates.text = "Failed to load USD rate"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            binding.tvRates.text = errorMsg
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

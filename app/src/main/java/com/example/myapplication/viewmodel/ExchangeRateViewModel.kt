package com.example.myapplication.viewmodel

import androidx.lifecycle.*
import com.example.myapplication.R
import com.example.myapplication.repository.ExchangeRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val repository: ExchangeRateRepository
) : ViewModel() {

    private val _usdRate = MutableLiveData<Double?>()
    val usdRate: LiveData<Double?> = _usdRate

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getRates() {
        viewModelScope.launch {
            try {
                val rate = repository.getUsdRate()
                if (rate != null) _usdRate.value = rate
                else _error.value = R.string.usd_rate_not_available.toString()
            } catch (e: Exception) {
                _error.value = "API Error: ${e.message}"
            }
        }
    }
}


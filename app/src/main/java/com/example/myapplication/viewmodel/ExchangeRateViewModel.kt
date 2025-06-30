package com.example.myapplication.viewmodel

import androidx.lifecycle.*
import com.example.myapplication.repository.ExchangeRateRepository
import com.example.myapplication.model.ExchangeRateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val repository: ExchangeRateRepository
) : ViewModel() {

    private val _exchangeRates = MutableLiveData<ExchangeRateResponse?>()
    val exchangeRates: LiveData<ExchangeRateResponse?> get() = _exchangeRates

    fun getRates(base: String) {
        viewModelScope.launch {
            try {
                val response = repository.getRates(base)
                if (response.isSuccessful) {
                    _exchangeRates.value = response.body()
                } else {
                    _exchangeRates.value = null
                }
            } catch (e: Exception) {
                _exchangeRates.value = null
            }
        }
    }

}

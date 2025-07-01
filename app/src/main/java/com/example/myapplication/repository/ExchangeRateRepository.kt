package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.BuildConfig
import com.example.myapplication.api.ExchangeRateApi
import javax.inject.Inject

class ExchangeRateRepository @Inject constructor(
    private val api: ExchangeRateApi
) {
    suspend fun getUsdRate(): Double? {
        val response = api.getRates("b5c5248d82a14384a17a563fd4fe19dc")
        return response.rates["ILS"]?.toDoubleOrNull()
    }
}


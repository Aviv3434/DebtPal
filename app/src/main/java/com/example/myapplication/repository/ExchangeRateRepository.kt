package com.example.myapplication.repository


import javax.inject.Inject
import com.example.myapplication.api.ExchangeRateApi
import com.example.myapplication.model.ExchangeRateResponse
import retrofit2.Response

class ExchangeRateRepository @Inject constructor(
    private val api: ExchangeRateApi
) {
    suspend fun getRates(base: String): Response<ExchangeRateResponse> {
        return api.getRates(base)
    }
}

package com.example.myapplication.repository


import com.example.myapplication.api.ExchangeRateApi
import com.example.myapplication.model.ExchangeRateResponse
import retrofit2.Response

class ExchangeRateRepository(private val api: ExchangeRateApi) {

    suspend fun getRates(base: String): Response<ExchangeRateResponse> {
        return api.getRates(base)
    }
}

package com.example.myapplication.api

import com.example.myapplication.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("v2.0/rates/latest")
    suspend fun getRates(
        @Query("apikey") apiKey: String
    ): ExchangeRateResponse
}


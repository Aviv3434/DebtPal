package com.example.myapplication.api

import com.example.myapplication.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("latest")
    suspend fun getRates(@Query("base") base: String): Response<ExchangeRateResponse>

    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): Response<ExchangeRateResponse>
}


package com.example.myapplication.network
import com.example.myapplication.model.ExchangeRateResponse


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("latest")
    suspend fun getLatestRates(@Query("base") base: String): ExchangeRateResponse

    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): ExchangeRateResponse
}

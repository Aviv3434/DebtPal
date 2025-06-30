package com.example.myapplication.api

import com.example.myapplication.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {

    @GET("latest")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<ExchangeRateResponse>
}

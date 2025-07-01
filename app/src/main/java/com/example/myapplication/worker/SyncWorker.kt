package com.example.myapplication.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.repository.ExchangeRateRepository

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    repository: ExchangeRateRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}
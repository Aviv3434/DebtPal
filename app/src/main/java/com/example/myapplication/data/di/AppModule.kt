package com.example.myapplication.data.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.DebtDao
import com.example.myapplication.data.DebtRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "debts_database"
        ).build()
    }

    @Provides
    fun provideDebtDao(database: AppDatabase): DebtDao {
        return database.debtDao()
    }

    @Provides
    @Singleton
    fun provideDebtRepository(debtDao: DebtDao): DebtRepository {
        return DebtRepository(debtDao)
    }
}

package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DebtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtItem)

    @Update
    suspend fun updateDebt(debt: DebtItem)

    @Delete
    suspend fun deleteDebt(debt: DebtItem)

    @Query("SELECT * FROM debts ORDER BY date DESC")
    fun getAllDebts(): LiveData<List<DebtItem>>

    @Query("SELECT * FROM debts WHERE is_settled = 0 ORDER BY date DESC")
    fun getUnsettledDebts(): LiveData<List<DebtItem>>

    @Query("SELECT * FROM debts WHERE is_settled = 1 ORDER BY date DESC")
    fun getSettledDebts(): LiveData<List<DebtItem>>

    @Query("SELECT * FROM debts WHERE payer = :user OR receiver = :user ORDER BY date DESC")
    fun getDebtsByUser(user: String): LiveData<List<DebtItem>>
}

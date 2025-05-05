package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.data.DebtDao
import com.example.myapplication.data.DebtItem

class DebtRepository(private val debtDao: DebtDao) {

    val allDebts: LiveData<List<DebtItem>> = debtDao.getAllDebts()
    val settledDebts: LiveData<List<DebtItem>> = debtDao.getSettledDebts()
    val unsettledDebts: LiveData<List<DebtItem>> = debtDao.getUnsettledDebts()

    fun getDebtsByUser(user: String): LiveData<List<DebtItem>> {
        return debtDao.getDebtsByUser(user)
    }

    suspend fun insertDebt(debt: DebtItem) {
        debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtItem) {
        debtDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtItem) {
        debtDao.deleteDebt(debt)
    }
}

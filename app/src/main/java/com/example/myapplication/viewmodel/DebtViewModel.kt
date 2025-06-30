package com.example.myapplication.viewmodel

import androidx.lifecycle.*
import com.example.myapplication.data.DebtItem
import com.example.myapplication.data.DebtRepository
import kotlinx.coroutines.launch

class DebtViewModel(private val repository: DebtRepository) : ViewModel() {

    val allDebts: LiveData<List<DebtItem>> = repository.allDebts
    val settledDebts: LiveData<List<DebtItem>> = repository.settledDebts
    val unsettledDebts: LiveData<List<DebtItem>> = repository.unsettledDebts
    val favoriteDebts: LiveData<List<DebtItem>> = repository.getFavoriteDebts()


    fun getDebtsByUser(user: String): LiveData<List<DebtItem>> {
        return repository.getDebtsByUser(user)
    }

    fun insertDebt(debt: DebtItem) = viewModelScope.launch {
        repository.insertDebt(debt)
    }

    fun updateDebt(debt: DebtItem) = viewModelScope.launch {
        repository.updateDebt(debt)
    }

    fun deleteDebt(debt: DebtItem) = viewModelScope.launch {
        repository.deleteDebt(debt)
    }
}

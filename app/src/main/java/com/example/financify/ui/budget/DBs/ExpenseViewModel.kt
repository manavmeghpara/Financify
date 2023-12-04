package com.example.financify.ui.budget.DBs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    val allExpensesLiveData: LiveData<List<Expense>> = repository.allExpenses.asLiveData()
    fun insertExpense(expense: Expense) {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expenseId: Long) {
        repository.deleteExpense(expenseId)
    }
}

class ExpenseViewModelFactory (private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(ExpenseViewModel::class.java))
            return ExpenseViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
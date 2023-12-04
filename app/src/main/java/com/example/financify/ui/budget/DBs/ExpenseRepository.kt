package com.example.financify.ui.budget.DBs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    fun insertExpense(expense: Expense) {
        CoroutineScope(IO).launch {
            expenseDao.insertExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        CoroutineScope(IO).launch {
            expenseDao.updateExpense(expense)
        }
    }

    fun deleteExpense(expenseId: Long) {
        CoroutineScope(IO).launch {
            expenseDao.deleteExpense(expenseId)
        }
    }
}
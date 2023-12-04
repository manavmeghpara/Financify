package com.example.financify.ui.budget.DBs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: List<Expense>)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY categoryName")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: Long)
}

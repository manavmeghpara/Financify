package com.example.financify.ui.savings.savingsDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financify.ui.stocks.stockDB.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert
    suspend fun insert(goal: GoalEntity)

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("SELECT * FROM goal")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("DELETE FROM goal WHERE name=:key")
    suspend fun deleteGoal(key: String)
}
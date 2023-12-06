package com.example.financify.ui.savings.savingsDB

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GoalRepository(private val goalDao: GoalDao) {

    val allEntry : Flow<List<GoalEntity>> = goalDao.getAllGoals()

    fun insert(goalEntity: GoalEntity){
        CoroutineScope(Dispatchers.IO).launch {
            goalDao.insert(goalEntity)
        }
    }

    fun update(goalEntity: GoalEntity){
        CoroutineScope(Dispatchers.IO).launch {
            goalDao.update(goalEntity)
        }
    }

    fun delete(key: String){
        CoroutineScope(Dispatchers.IO).launch {
            goalDao.deleteGoal(key)
        }
    }

}
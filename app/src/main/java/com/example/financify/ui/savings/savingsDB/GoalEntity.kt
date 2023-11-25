package com.example.financify.ui.savings.savingsDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var type: String = "",

    var name: String = "",

    var amount: Double = 0.0,

    var progress: Double = 0.0
)
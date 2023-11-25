package com.example.financify.ui.dashboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var name: String,

    var amount: Int
)
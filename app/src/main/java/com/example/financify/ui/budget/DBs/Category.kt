package com.example.financify.ui.budget.DBs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var amount: Int,
) {
    override fun toString(): String {
        return name
    }
}
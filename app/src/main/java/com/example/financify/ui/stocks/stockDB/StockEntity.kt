package com.example.financify.ui.stocks.stockDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var symbol: String = "",

    var shares: Int = 0,
)
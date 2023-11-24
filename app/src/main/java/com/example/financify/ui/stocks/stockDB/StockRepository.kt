package com.example.financify.ui.stocks.stockDB

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StockRepository(private val stockDao: StockDao) {
    val allEntry : Flow<List<StockEntity>> = stockDao.getAllStocks()

    fun insert(stockEntity: StockEntity){
        CoroutineScope(Dispatchers.IO).launch {
            stockDao.insert(stockEntity)
        }
    }

    fun delete(key: String){
        CoroutineScope(Dispatchers.IO).launch {
            stockDao.deleteStock(key)
        }
    }

}
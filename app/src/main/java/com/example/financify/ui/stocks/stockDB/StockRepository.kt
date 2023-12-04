package com.example.financify.ui.stocks.stockDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StockRepository(private val stockDao: StockDao) {
    val allEntry : Flow<List<StockEntity>> = stockDao.getAllStocks()

    fun getAllStocks(): LiveData<List<StockEntity>>{
        return stockDao.getAllStocks().asLiveData()
    }
    fun insert(stockEntity: StockEntity){
        CoroutineScope(Dispatchers.IO).launch {
            stockDao.insert(stockEntity)
        }
    }

    fun update(stockEntity: StockEntity){
        CoroutineScope(Dispatchers.IO).launch {
            stockDao.update(stockEntity)
        }
    }

    fun delete(key: String){
        CoroutineScope(Dispatchers.IO).launch {
            stockDao.deleteStock(key)
        }
    }

}
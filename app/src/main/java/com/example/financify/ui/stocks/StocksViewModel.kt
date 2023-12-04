package com.example.financify.ui.stocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.financify.ui.stocks.stockDB.StockEntity
import com.example.financify.ui.stocks.stockDB.StockRepository

class StocksViewModel(private val repository: StockRepository) : ViewModel() {

    val stockList : LiveData<List<StockEntity>> = repository.allEntry.asLiveData()

    fun insert(entry: StockEntity){
        repository.insert(entry)
    }
    fun delete(key: String){
        repository.delete(key)
    }

    fun update(e: StockEntity){
        repository.update(e)
    }

}

class StocksViewModelFactory (private val repository: StockRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StocksViewModel::class.java))
            return StocksViewModel(repository) as T
        throw IllegalArgumentException("Unknown View Model!")
    }
}

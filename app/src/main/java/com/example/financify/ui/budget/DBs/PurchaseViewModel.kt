package com.example.financify.ui.budget.DBs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class PurchaseViewModel(private val repository: PurchaseRepository) : ViewModel() {
    val allPurchasesLiveData: LiveData<List<Purchase>> = repository.allPurchases.asLiveData()
    fun insertPurchase(purchase: Purchase) {
        repository.insertPurchase(purchase)
    }

    fun updatePurchase(purchase: Purchase) {
        repository.updatePurchase(purchase)
    }

    fun deletePurchase(purchaseId: Long) {
        repository.deletePurchase(purchaseId)
    }
}

class PurchaseViewModelFactory (private val repository: PurchaseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(PurchaseViewModel::class.java))
            return PurchaseViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
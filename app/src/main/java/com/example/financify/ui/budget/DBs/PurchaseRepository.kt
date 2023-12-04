package com.example.financify.ui.budget.DBs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PurchaseRepository(private val purchaseDao: PurchaseDao) {
    val allPurchases: Flow<List<Purchase>> = purchaseDao.getAllPurchases()
    fun insertPurchase(purchase: Purchase) {
        CoroutineScope(IO).launch {
            purchaseDao.insertPurchase(purchase)
        }
    }

    fun updatePurchase(purchase: Purchase) {
        CoroutineScope(IO).launch {
            purchaseDao.updatePurchase(purchase)
        }
    }

    fun deletePurchase(purchaseId: Long) {
        CoroutineScope(IO).launch {
            purchaseDao.deletePurchase(purchaseId)
        }
    }
}
package com.example.financify.ui.budget.DBs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: List<Purchase>)

    @Update
    suspend fun updatePurchase(purchase: Purchase)

    @Query("SELECT * FROM purchases ORDER BY categoryName")
    fun getAllPurchases(): Flow<List<Purchase>>

    @Query("DELETE FROM purchases WHERE id = :purchaseId")
    suspend fun deletePurchase(purchaseId: Long)
}

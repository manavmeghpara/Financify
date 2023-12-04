package com.example.financify.ui.budget.DBs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE name = :categoryName")
    fun getCategoryByName(categoryName: String): Category?

    @Query("DELETE FROM categories WHERE name = :categoryName")
    suspend fun deleteCategory(categoryName: String)
}

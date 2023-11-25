package com.example.financify.ui.dashboard

import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allExercises: Flow<List<Category>> = categoryDao.getAllCategories()
    fun insertCategory(category: Category) {
        CoroutineScope(IO).launch {
            categoryDao.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        CoroutineScope(IO).launch {
            categoryDao.updateCategory(category)
        }
    }

    fun getCategoryByName(categoryName: String): Category? {
        return categoryDao.getCategoryByName(categoryName)
    }

    fun deleteCategory(categoryId: Long) {
        CoroutineScope(IO).launch {
            categoryDao.deleteCategory(categoryId)
        }
    }
}
package com.example.financify.ui.budget.DBs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {
    val allCategoriesLiveData: LiveData<List<Category>> = repository.allExercises.asLiveData()
    fun insertCategory(category: Category) {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) {
        repository.updateCategory(category)
    }

    fun getCategoryByName(categoryName: String): Category? {
        return repository.getCategoryByName(categoryName)
    }

    fun deleteCategory(categoryName: String) {
        repository.deleteCategory(categoryName)
    }
}

class CategoryViewModelFactory (private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(CategoryViewModel::class.java))
            return CategoryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
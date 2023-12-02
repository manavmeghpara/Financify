package com.example.financify.ui.savings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.financify.ui.savings.savingsDB.GoalEntity
import com.example.financify.ui.savings.savingsDB.GoalRepository
import kotlinx.coroutines.launch

class SavingsViewModel(private val repository: GoalRepository) : ViewModel() {

    val goalListLive : LiveData<List<GoalEntity>> = repository.allEntry.asLiveData()

    fun insert(entry: GoalEntity){
        repository.insert(entry)
    }
    fun delete(key: String){
        repository.delete(key)
    }
    fun UpdateGoal(editedGoal: GoalEntity) {
        repository.update(editedGoal)
    }
}

class SavingsViewModelFactory (private val repository: GoalRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SavingsViewModel::class.java))
            return SavingsViewModel(repository) as T
        throw IllegalArgumentException("Unknown View Model!")
    }
}

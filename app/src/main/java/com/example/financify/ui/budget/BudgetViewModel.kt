package com.example.financify.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BudgetViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is BUDGET Fragment"
    }
    val text: LiveData<String> = _text
}
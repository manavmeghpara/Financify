package com.example.financify.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.finnhub.api.models.Quote

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Navigation Fragment"
    }
    val text: LiveData<String> = _text
}
package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.divelog.data.DiveRepository

class DiveViewModelFactory(
    private val repository: DiveRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiveViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
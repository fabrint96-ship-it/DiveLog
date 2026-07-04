package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.divelog.data.DiveMediaNoteRepository

class DiveMediaNoteViewModelFactory(
    private val repository: DiveMediaNoteRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(DiveMediaNoteViewModel::class.java)) {
            return DiveMediaNoteViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
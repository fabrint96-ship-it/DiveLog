package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.DiveRepository
import com.example.divelog.data.model.Dive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiveViewModel(
    private val repository: DiveRepository
) : ViewModel() {

    val dives = repository.dives.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addDive(dive: Dive) {
        viewModelScope.launch {
            repository.insertDive(dive)
        }
    }

    fun deleteDive(dive: Dive) {
        viewModelScope.launch {
            repository.deleteDive(dive)
        }
    }

    fun updateDive(dive: Dive) {
        viewModelScope.launch {
            repository.updateDive(dive)
        }
    }
}
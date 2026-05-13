package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.DiveRepository
import com.example.divelog.domain.model.Dive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.divelog.domain.usecase.AddDiveUseCase
import com.example.divelog.domain.usecase.DeleteDiveUseCase
import com.example.divelog.domain.usecase.GetDivesUseCase
import com.example.divelog.domain.usecase.UpdateDiveUseCase

class DiveViewModel(
    private val getDivesUseCase: GetDivesUseCase,
    private val addDiveUseCase: AddDiveUseCase,
    private val updateDiveUseCase: UpdateDiveUseCase,
    private val deleteDiveUseCase: DeleteDiveUseCase
) : ViewModel() {

    val dives = getDivesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addDive(dive: Dive) {
        viewModelScope.launch {
            addDiveUseCase(dive)
        }
    }

    fun deleteDive(dive: Dive) {
        viewModelScope.launch {
            deleteDiveUseCase(dive)
        }
    }

    fun updateDive(dive: Dive) {
        viewModelScope.launch {
            updateDiveUseCase(dive)
        }
    }
}
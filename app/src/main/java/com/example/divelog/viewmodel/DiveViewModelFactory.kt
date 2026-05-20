package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.divelog.data.DiveRepository
import com.example.divelog.domain.usecase.AddDiveUseCase
import com.example.divelog.domain.usecase.DeleteDiveUseCase
import com.example.divelog.domain.usecase.GetDivesUseCase
import com.example.divelog.domain.usecase.UpdateDiveUseCase

class DiveViewModelFactory(
    private val repository: DiveRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiveViewModel(
                getDivesUseCase = GetDivesUseCase(repository),
                addDiveUseCase = AddDiveUseCase(repository),
                updateDiveUseCase = UpdateDiveUseCase(repository),
                deleteDiveUseCase = DeleteDiveUseCase(repository)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
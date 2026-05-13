package com.example.divelog.domain.usecase

import com.example.divelog.data.DiveRepository
import com.example.divelog.domain.model.Dive

class UpdateDiveUseCase(
    private val repository: DiveRepository
) {
    suspend operator fun invoke(dive: Dive) {
        repository.updateDive(dive)
    }
}
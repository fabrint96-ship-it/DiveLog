package com.example.divelog.domain.usecase

import com.example.divelog.data.DiveRepository
import com.example.divelog.domain.model.Dive

class DeleteDiveUseCase(
    private val repository: DiveRepository
) {
    suspend operator fun invoke(dive: Dive) {
        repository.deleteDive(dive)
    }
}
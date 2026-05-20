package com.example.divelog.domain.usecase

import com.example.divelog.data.DiveRepository
import com.example.divelog.domain.model.Dive
import kotlinx.coroutines.flow.Flow

class GetDivesUseCase(
    private val repository: DiveRepository
) {
    operator fun invoke(): Flow<List<Dive>> {
        return repository.dives
    }
}
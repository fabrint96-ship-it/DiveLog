package com.example.divelog.data

import com.example.divelog.data.local.DiveDao
import com.example.divelog.data.model.Dive
import kotlinx.coroutines.flow.Flow

class DiveRepository(
    private val diveDao: DiveDao
) {
    val dives: Flow<List<Dive>> = diveDao.getAllDives()

    suspend fun insertDive(dive: Dive) {
        diveDao.insertDive(dive)
    }

    suspend fun deleteDive(dive: Dive) {
        diveDao.deleteDive(dive)
    }
}
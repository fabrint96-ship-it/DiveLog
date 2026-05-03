package com.example.divelog.data.local
import androidx.room.Update

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.divelog.data.model.Dive
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete

@Dao
interface DiveDao {

    @Query("SELECT * FROM dives ORDER BY id DESC")
    fun getAllDives(): Flow<List<Dive>>

    @Insert
    suspend fun insertDive(dive: Dive)

    @Delete
    suspend fun deleteDive(dive: Dive)

    @Update
    suspend fun updateDive(dive: Dive)
}
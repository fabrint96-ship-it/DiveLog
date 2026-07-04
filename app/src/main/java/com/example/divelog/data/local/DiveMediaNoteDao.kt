package com.example.divelog.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divelog.domain.model.DiveMediaNote
import kotlinx.coroutines.flow.Flow

@Dao
interface DiveMediaNoteDao {

    @Query("SELECT * FROM dive_media_notes WHERE mediaUri = :mediaUri LIMIT 1")
    fun getNoteByMediaUri(mediaUri: String): Flow<DiveMediaNote?>

    @Query("DELETE FROM dive_media_notes WHERE mediaUri = :mediaUri")
    suspend fun deleteByMediaUri(mediaUri: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: DiveMediaNote)
}
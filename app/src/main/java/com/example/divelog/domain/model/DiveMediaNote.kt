package com.example.divelog.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "dive_media_notes")
data class DiveMediaNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val diveId: Int,
    val mediaUri: String,
    val mediaType: String,
    val note: String,
    val syncId: String = UUID.randomUUID().toString()
)
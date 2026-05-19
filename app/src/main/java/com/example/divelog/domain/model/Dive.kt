package com.example.divelog.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "dives")
data class Dive(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val location: String = "",
    val date: String = "",
    val maxDepth: String = "",
    val duration: String = "",
    val waterTemperature: String = "",
    val visibility: String = "",
    val notes: String = "",
    val diveType: String = "",
    val photos: List<String> = emptyList(),
    val drawings: List<String> = emptyList(),
    val cloudId: Long? = null,
    val syncId: String = java.util.UUID.randomUUID().toString()
)
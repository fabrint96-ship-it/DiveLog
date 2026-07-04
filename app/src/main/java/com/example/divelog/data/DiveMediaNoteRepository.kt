package com.example.divelog.data

import com.example.divelog.data.local.DiveMediaNoteDao
import com.example.divelog.domain.model.DiveMediaNote
import kotlinx.coroutines.flow.Flow

class DiveMediaNoteRepository(
    private val dao: DiveMediaNoteDao
) {
    fun getNoteByMediaUri(mediaUri: String): Flow<DiveMediaNote?> {
        return dao.getNoteByMediaUri(mediaUri)
    }

    suspend fun saveNote(
        diveId: Int,
        mediaUri: String,
        mediaType: String,
        noteText: String
    ) {
        dao.deleteByMediaUri(mediaUri)

        dao.insert(
            DiveMediaNote(
                diveId = diveId,
                mediaUri = mediaUri,
                mediaType = mediaType,
                note = noteText
            )
        )
    }

    suspend fun deleteNoteByMediaUri(mediaUri: String) {
        dao.deleteByMediaUri(mediaUri)
    }
}
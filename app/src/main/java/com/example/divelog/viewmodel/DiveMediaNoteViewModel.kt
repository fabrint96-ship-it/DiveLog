package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.DiveMediaNoteRepository
import com.example.divelog.domain.model.DiveMediaNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiveMediaNoteViewModel(
    private val repository: DiveMediaNoteRepository
) : ViewModel() {

    private val _note = MutableStateFlow<DiveMediaNote?>(null)
    val note: StateFlow<DiveMediaNote?> = _note

    fun loadNote(mediaUri: String) {
        viewModelScope.launch {
            repository.getNoteByMediaUri(mediaUri).collectLatest { mediaNote ->
                _note.value = mediaNote
            }
        }
    }

    fun saveNote(
        diveId: Int,
        mediaUri: String,
        mediaType: String,
        noteText: String
    ) {
        viewModelScope.launch {
            repository.saveNote(
                diveId = diveId,
                mediaUri = mediaUri,
                mediaType = mediaType,
                noteText = noteText
            )
        }
    }

    fun deleteNote(mediaUri: String) {
        viewModelScope.launch {
            repository.deleteNoteByMediaUri(mediaUri)
        }
    }
}
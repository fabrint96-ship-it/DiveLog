package com.example.divelog.viewmodel

import com.example.divelog.domain.model.Dive

data class DiveUiState(
    val isLoading: Boolean = false,
    val dives: List<Dive> = emptyList(),
    val errorMessage: String? = null,

    val isSyncing: Boolean = false,
    val syncMessage: String? = null
)
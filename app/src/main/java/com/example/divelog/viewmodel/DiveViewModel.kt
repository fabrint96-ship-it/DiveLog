package com.example.divelog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.remote.CloudBackupRepository
import com.example.divelog.domain.model.Dive
import com.example.divelog.domain.usecase.AddDiveUseCase
import com.example.divelog.domain.usecase.DeleteDiveUseCase
import com.example.divelog.domain.usecase.GetDivesUseCase
import com.example.divelog.domain.usecase.UpdateDiveUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiveViewModel(
    private val getDivesUseCase: GetDivesUseCase,
    private val addDiveUseCase: AddDiveUseCase,
    private val updateDiveUseCase: UpdateDiveUseCase,
    private val deleteDiveUseCase: DeleteDiveUseCase
) : ViewModel() {

    private val cloudBackupRepository = CloudBackupRepository()

    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isSyncing = MutableStateFlow(false)

    private val _syncEvents = MutableSharedFlow<String>()
    val syncEvents: SharedFlow<String> = _syncEvents.asSharedFlow()

    private var hasRestoredAfterLogin = false

    val uiState: StateFlow<DiveUiState> = combine(
        getDivesUseCase(),
        _isLoading,
        _errorMessage,
        _isSyncing
    ) { dives, isLoading, errorMessage, isSyncing ->
        DiveUiState(
            dives = dives,
            isLoading = isLoading,
            errorMessage = errorMessage,
            isSyncing = isSyncing,
            syncMessage = null
        )
    }
        .catch { exception ->
            Log.e("DIVE_VIEWMODEL", "Error cargando datos locales", exception)

            emit(
                DiveUiState(
                    dives = emptyList(),
                    isLoading = false,
                    errorMessage = exception.message ?: "Error desconocido",
                    isSyncing = false,
                    syncMessage = null
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiveUiState(isLoading = true)
        )

    init {
        _isLoading.value = false
    }

    fun addDive(dive: Dive) {
        viewModelScope.launch {
            try {
                addDiveUseCase(dive)
            } catch (e: Exception) {
                Log.e("DIVE_VIEWMODEL", "Error añadiendo inmersión", e)
                _syncEvents.emit("Error añadiendo inmersión")
            }
        }
    }

    fun updateDive(dive: Dive) {
        viewModelScope.launch {
            try {
                updateDiveUseCase(dive)
            } catch (e: Exception) {
                Log.e("DIVE_VIEWMODEL", "Error actualizando inmersión", e)
                _syncEvents.emit("Error actualizando inmersión")
            }
        }
    }

    fun deleteDive(dive: Dive) {
        viewModelScope.launch {
            try {
                deleteDiveUseCase(dive)
            } catch (e: Exception) {
                Log.e("DIVE_VIEWMODEL", "Error eliminando inmersión", e)
                _syncEvents.emit("Error eliminando inmersión")
            }
        }
    }

    fun backupToCloud() {
        if (_isSyncing.value) return

        _isSyncing.value = true

        viewModelScope.launch {
            try {
                val currentDives = uiState.value.dives

                cloudBackupRepository.uploadDives(currentDives)

                _syncEvents.emit("Backup completado")

                Log.d(
                    "SUPABASE_BACKUP",
                    "Backup completado: ${currentDives.size} inmersiones"
                )
            } catch (e: Exception) {
                Log.e("SUPABASE_BACKUP", "Error backup", e)
                _syncEvents.emit("Error en backup")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun restoreFromCloud() {
        if (_isSyncing.value) return

        _isSyncing.value = true

        viewModelScope.launch {
            try {
                val cloudDives = cloudBackupRepository.downloadDives()

                val uniqueCloudDives = cloudDives
                    .filter { it.syncId.isNotBlank() }
                    .distinctBy { it.syncId }

                val localDives = uiState.value.dives

                localDives.forEach { dive ->
                    deleteDiveUseCase(dive)
                }

                uniqueCloudDives.forEach { dive ->
                    addDiveUseCase(
                        dive.copy(id = 0)
                    )
                }

                _syncEvents.emit("Restore completado")

                Log.d(
                    "SUPABASE_RESTORE",
                    "Restore completado: ${uniqueCloudDives.size} inmersiones"
                )
            } catch (e: Exception) {
                Log.e("SUPABASE_RESTORE", "Error restore", e)
                _syncEvents.emit("Error restaurando datos")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun restoreAfterLoginIfNeeded() {
        if (hasRestoredAfterLogin) return
        if (_isSyncing.value) return

        hasRestoredAfterLogin = true
        restoreFromCloud()
    }

    fun resetAutoRestoreFlag() {
        hasRestoredAfterLogin = false
    }

    fun clearErrorMessage() {
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }
}
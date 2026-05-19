package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.DiveRepository
import com.example.divelog.data.remote.CloudBackupRepository
import com.example.divelog.domain.model.Dive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.divelog.domain.usecase.AddDiveUseCase
import com.example.divelog.domain.usecase.DeleteDiveUseCase
import com.example.divelog.domain.usecase.GetDivesUseCase
import com.example.divelog.domain.usecase.UpdateDiveUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

class DiveViewModel(
    private val getDivesUseCase: GetDivesUseCase,
    private val addDiveUseCase: AddDiveUseCase,
    private val updateDiveUseCase: UpdateDiveUseCase,
    private val deleteDiveUseCase: DeleteDiveUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val cloudBackupRepository = CloudBackupRepository()

    val uiState: StateFlow<DiveUiState> = combine(
        getDivesUseCase(),
        _isLoading,
        _errorMessage
    ) { dives, isLoading, errorMessage ->
        DiveUiState(
            isLoading = isLoading,
            dives = dives,
            errorMessage = errorMessage
        )
    }
        .catch { exception ->
            emit(
                DiveUiState(
                    isLoading = false,
                    errorMessage = exception.message ?: "Error desconocido"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiveUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            _isLoading.value = false
        }
    }

    fun addDive(dive: Dive) {
        viewModelScope.launch {
            addDiveUseCase(dive)
        }
    }

    fun deleteDive(dive: Dive) {
        viewModelScope.launch {
            deleteDiveUseCase(dive)
        }
    }

    fun updateDive(dive: Dive) {
        viewModelScope.launch {
            updateDiveUseCase(dive)
        }
    }

    fun backupToCloud() {
        viewModelScope.launch {
            try {
                val currentDives = uiState.value.dives

                cloudBackupRepository.uploadDives(currentDives)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun restoreFromCloud() {
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

            } catch (e: Exception) {
                android.util.Log.e("SUPABASE_RESTORE", "Error restore", e)
            }
        }
    }
}
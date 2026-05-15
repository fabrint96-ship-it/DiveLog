package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isLoggedIn = authRepository.isUserLoggedIn
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                authRepository.login(email, password)
                _uiState.value = AuthUiState(isLoggedIn = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                authRepository.register(email, password)
                _uiState.value = AuthUiState(isLoggedIn = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al registrar usuario"
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }
}
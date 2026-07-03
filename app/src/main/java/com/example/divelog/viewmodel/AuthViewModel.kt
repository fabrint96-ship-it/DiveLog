package com.example.divelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.divelog.data.remote.SupabaseAuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import com.example.divelog.data.remote.SupabaseClientProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val authRepository = SupabaseAuthRepository()
    private val client = SupabaseClientProvider.client

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            client.auth.sessionStatus.collect { status ->
                _uiState.value = when (status) {
                    is SessionStatus.Authenticated -> {
                        AuthUiState(
                            isLoading = false,
                            isLoggedIn = true
                        )
                    }

                    is SessionStatus.NotAuthenticated -> {
                        AuthUiState(
                            isLoading = false,
                            isLoggedIn = false
                        )
                    }

                    else -> {
                        AuthUiState(
                            isLoading = true,
                            isLoggedIn = false
                        )
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                authRepository.login(email, password)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = e.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                authRepository.register(email, password)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = e.message ?: "Error al registrar usuario"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(
                isLoading = false,
                isLoggedIn = false
            )
        }
    }
}
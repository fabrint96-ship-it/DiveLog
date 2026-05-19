package com.example.divelog.data.remote

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email


class SupabaseAuthRepository {

    private val client = SupabaseClientProvider.client

    val isUserLoggedIn: Boolean
        get() = client.auth.currentSessionOrNull() != null

    suspend fun login(
        email: String,
        password: String
    ) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun register(
        email: String,
        password: String
    ) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }

    fun currentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }
}
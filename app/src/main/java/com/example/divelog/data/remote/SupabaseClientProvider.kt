package com.example.divelog.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://tashvzbmuzttvahbwgtx.supabase.co"

    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRhc2h2emJtdXp0dHZhaGJ3Z3R4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzkwNDg3NTQsImV4cCI6MjA5NDYyNDc1NH0.VPfX174mkiRxaclsrsDeqZtZOESrK_bJx1HYTTtSDq4"
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {

        install(Auth) {
            autoLoadFromStorage = true
            autoSaveToStorage = true
        }

        install(Postgrest)
        install(Storage)
    }
}
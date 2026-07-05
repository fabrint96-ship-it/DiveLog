package com.example.divelog.data.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ReverseGeocoder {

    suspend fun getLocationName(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val address = addresses.firstOrNull()

                        continuation.resume(
                            address?.let {
                                listOfNotNull(
                                    it.locality,
                                    it.adminArea,
                                    it.countryName
                                ).joinToString(", ")
                            }.orEmpty()
                        )
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val address = geocoder.getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()

                address?.let {
                    listOfNotNull(
                        it.locality,
                        it.adminArea,
                        it.countryName
                    ).joinToString(", ")
                }.orEmpty()
            }
        } catch (e: Exception) {
            ""
        }
    }
}
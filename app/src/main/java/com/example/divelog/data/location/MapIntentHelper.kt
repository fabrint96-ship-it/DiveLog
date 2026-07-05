package com.example.divelog.data.location

import android.content.Context
import android.content.Intent
import android.net.Uri

object MapIntentHelper {

    fun openLocation(
        context: Context,
        latitude: Double,
        longitude: Double,
        label: String = "DiveLog"
    ) {
        val uri = Uri.parse(
            "geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)

        context.startActivity(intent)
    }
}
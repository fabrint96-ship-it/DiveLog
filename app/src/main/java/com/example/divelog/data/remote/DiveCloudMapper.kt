package com.example.divelog.data.remote

import com.example.divelog.domain.model.Dive

fun Dive.toSupabaseDto(userId: String): SupabaseDiveDto {
    return SupabaseDiveDto(
        localId = id,
        userId = userId,
        title = title,
        location = location,
        date = date,
        maxDepth = maxDepth,
        duration = duration,
        waterTemperature = waterTemperature,
        visibility = visibility,
        notes = notes,
        diveType = diveType,
        photos = photos,
        drawings = drawings,
        syncId = syncId,
        latitude = latitude,
        longitude = longitude,
    )
}

fun SupabaseDiveDto.toDive(): Dive {
    return Dive(
        title = title ?: "",
        location = location ?: "",
        date = date ?: "",
        maxDepth = maxDepth ?: "",
        duration = duration ?: "",
        waterTemperature = waterTemperature ?: "",
        visibility = visibility ?: "",
        notes = notes ?: "",
        diveType = diveType ?: "",
        photos = photos ?: emptyList(),
        drawings = drawings ?: emptyList(),
        syncId = syncId,
        latitude = latitude,
        longitude = longitude,
    )
}
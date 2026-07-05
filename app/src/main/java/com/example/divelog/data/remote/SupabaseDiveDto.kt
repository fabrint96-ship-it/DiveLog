package com.example.divelog.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseDiveDto(

    val id: Long? = null,

    @SerialName("user_id")
    val userId: String? = null,

    val title: String? = null,
    val location: String? = null,
    val date: String? = null,

    @SerialName("max_depth")
    val maxDepth: String? = null,

    val duration: String? = null,

    @SerialName("water_temperature")
    val waterTemperature: String? = null,

    val visibility: String? = null,
    val notes: String? = null,

    @SerialName("dive_type")
    val diveType: String? = null,

    val photos: List<String>? = null,
    val drawings: List<String>? = null,

    @SerialName("local_id")
    val localId: Int? = null,

    @SerialName("sync_id")
    val syncId: String = "",

    val latitude: Double? = null,
    val longitude: Double? = null,

)
package com.example.divelog.data.model

data class GalleryItem(
    val uri: String,
    val type: GalleryItemType
)

enum class GalleryItemType {
    PHOTO,
    DRAWING
}
package com.example.divelog.domain.model

data class GalleryItem(
    val uri: String,
    val type: GalleryItemType
)

enum class GalleryItemType {
    PHOTO,
    DRAWING
}
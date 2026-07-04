package com.example.divelog.navigation

object Routes {
    const val LOGIN = "login"
    const val DIVE_LIST = "dive_list"
    const val ADD_DIVE = "add_dive"
    const val DIVE_DETAIL = "dive_detail/{diveId}"
    const val EDIT_DIVE = "edit_dive/{diveId}"
    const val DRAWING = "drawing/{diveId}"
    const val PHOTO_VIEWER = "photo_viewer/{diveId}/{photoUri}"

    const val AUTH_LOADING = "auth_loading"

    fun diveDetail(diveId: Int): String = "dive_detail/$diveId"
    fun editDive(diveId: Int): String = "edit_dive/$diveId"

    fun drawing(diveId: Int): String = "drawing/$diveId"
    fun photoViewer(diveId: Int, photoUri: String): String =
        "photo_viewer/$diveId/$photoUri"
}
package com.example.divelog.navigation

object Routes {
    const val DIVE_LIST = "dive_list"
    const val ADD_DIVE = "add_dive"
    const val DIVE_DETAIL = "dive_detail/{diveId}"
    const val DRAWING = "drawing"

    fun diveDetail(diveId: Int): String = "dive_detail/$diveId"
}
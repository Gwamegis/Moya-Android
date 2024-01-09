package com.soi.moya.models

data class Music(
    val id: String = "",
    val info: String = "",
    val lyrics: String = "",
    val title: String = "",
    val type: Boolean = false,
    val url: String = ""
)

// TODO: 삭제 필요
val musicList = List(10) {
    Music(title = "test$it", info = "subTitle$it")
}
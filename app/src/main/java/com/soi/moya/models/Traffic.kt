package com.soi.moya.models

import java.util.Date

data class Traffic(
    val date: Date = Date(),
    val title: String = "",
    val description: String = "",
)
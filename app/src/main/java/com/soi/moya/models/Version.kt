package com.soi.moya.models

data class Version(
    val version: String = "1.0.0",
    val isRequired: Boolean = false,
    val feature: List<String> = listOf(),
)
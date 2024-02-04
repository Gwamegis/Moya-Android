package com.soi.moya.models

data class Version(
    val version: String = "",
    val isRequired: Boolean = false,
    val feature: List<String>
)
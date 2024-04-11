package com.soi.moya.models

import com.google.firebase.firestore.PropertyName

data class Version(
    val version: String = "1.0.0",
    val feature: List<String> = listOf(),

    // is로 시작되는 Boolean 변수가 파싱이 제대로 안 되는 문제 해결
    @get:PropertyName("isRequired")
    val isRequired: Boolean = false
)
package com.soi.moya.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Main Screen", fontSize = 24.sp)
    }
}
package com.soi.moya.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.ui.main.MainScreen

class MainActivity : BaseComposeActivity() {
    @Composable
    override fun Content() {
        MainScreen()
    }
}
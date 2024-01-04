package com.soi.moya.ui

import androidx.compose.runtime.Composable
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.ui.bottom_nav.BottomNavScreen

class MainActivity : BaseComposeActivity() {
    @Composable
    override fun Content() {
        BottomNavScreen()
    }
}
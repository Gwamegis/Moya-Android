package com.soi.moya.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object MoyaTheme {
    val colors: MoyaTeamColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
}
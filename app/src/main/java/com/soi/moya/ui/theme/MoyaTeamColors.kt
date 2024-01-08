package com.soi.moya.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class MoyaTeamColors(
    background: Color,
    point: Color,
    sub: Color
) {
    var background by mutableStateOf(background)
        private set

    var point by mutableStateOf(point)
        private set

    var sub by mutableStateOf(sub)
        private set

    fun copy(
        background: Color = this.background,
        point: Color = this.point,
        sub: Color = this.sub
    ) = MoyaTeamColors(
        background = background,
        point = point,
        sub = sub
    )
    fun updateColorsFrom(other: MoyaTeamColors) {
        background = other.background
        point = other.point
        sub = other.sub
    }
}

val LocalColors = staticCompositionLocalOf {
    initColors()
}

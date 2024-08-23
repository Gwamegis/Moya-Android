package com.soi.moya.ui.music_player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun MusicLyricScreen (
    modifier: Modifier = Modifier,
    music: MusicInfo,
) {
    val scrollState = rememberScrollState()
    val gradientTopColor = scrollState.value > 10
    val gradientBottomColor = scrollState.value < scrollState.maxValue

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 40.dp)
    ) {
        Text(
            text = music.lyrics.replace("\\n", "\n"),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 73.dp),
            style = getTextStyle(style = MoyaFont.CustomHeadlineBold),
            color = MoyaColor.white.copy(0.8f)
        )

        GradientBox(
            modifier = Modifier
                .align(Alignment.TopCenter),
            isVisible = gradientTopColor,
            gradientColors = listOf(
                music.team.getSubColor(),
                music.team.getSubColor().copy(0.3f)
            )
        )

        GradientBox(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            isVisible = gradientBottomColor,
            gradientColors = listOf(
                music.team.getSubColor().copy(0.3f),
                music.team.getSubColor()
            )
        )
    }
}

@Composable
fun GradientBox(
    modifier: Modifier,
    isVisible: Boolean,
    gradientColors: List<Color>
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )

        )
    }
}
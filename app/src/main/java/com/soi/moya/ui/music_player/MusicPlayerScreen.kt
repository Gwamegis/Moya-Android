package com.soi.moya.ui.music_player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.models.Music
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun MusicPlayerScreen() {
    val music = Music(
        title = "Test title",
        info = "test team name",
        lyrics = "나는 행복합니다\n나는 행복합니다\n나는 행복합니다\n이글스라 행복합니다\n\n나는 행복합니다\n나는 행복합니다\n나는 행복합니다" +
                "나는 행복합니다\n" +
                "나는 행복합니다\n" +
                "이글스라 행복합니다\n" +
                "\n" +
                "나는 행복합니다\n" +
                "나는 행복합니다\n" +
                "나는 행복합니다\n" +
                "한화라서 행복합니다\n" +
                "나는 행복합니다\n"
    )

    val progress = remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .background(MoyaColor.doosanSub)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        MusicNavigationBar(
            music = music,
            isLike = true
        )

        MusicLylicView(
            music = music,
            modifier = Modifier.weight(1f)
        )

        MusicPlayerSlider(
            progress = progress.value,
            onProgressChanged = { newProgress ->
                progress.value = newProgress
            }
        )

        MusicPlayerBottomButtonView(
            onClickPlayButton = { }
        )
    }
}

@Composable
fun MusicNavigationBar(music: Music, isLike: Boolean) {
    val likeIcon = if (isLike) {
        R.drawable.heart_fill
    } else {
        R.drawable.heart
    }

    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        IconButton(
            modifier = Modifier
                .size(20.dp),
            onClick = { /*TODO*/ }) {

            Icon(
                painterResource(id = R.drawable.chevron_left),
                contentDescription = null,
                tint = MoyaColor.white
            )
        }

        Image(
            modifier = Modifier
                .size(52.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
            painter = painterResource(id = R.drawable.album_doosan),
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .weight(1f)
        ) {

            Text(
                text = music.title,
                style = getTextStyle(style = MoyaFont.CustomBodyBold),
                color = MoyaColor.white
            )

            Text(
                text = music.info,
                style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
                color = MoyaColor.gray
            )
        }

        IconButton(
            modifier = Modifier
                .size(26.dp),
            onClick = { /*TODO*/ }) {
            Icon(
                painterResource(id = likeIcon),
                contentDescription = null,
                tint = MoyaColor.doosanPoint
            )
        }
    }
}

@Composable
fun MusicLylicView(
    modifier: Modifier,
    music: Music
) {
    val scrollState = rememberScrollState()
    val gradientTopColor = scrollState.value > 0
    val gradientBottomColor = scrollState.value < scrollState.maxValue

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = music.lyrics,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            style = getTextStyle(style = MoyaFont.CustomHeadlineBold),
            color = MoyaColor.white
        )

        GradientBox(
            modifier = Modifier
                .align(Alignment.TopCenter),
            isVisible = gradientTopColor,
            gradientColors = listOf(
                MoyaColor.doosanSub,
                MoyaColor.doosanSub.copy(0.3f)
            )
        )

        GradientBox(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            isVisible = gradientBottomColor,
            gradientColors = listOf(
                MoyaColor.doosanSub.copy(0.3f),
                MoyaColor.doosanSub
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

@Composable
fun MusicPlayerSlider(
    progress: Float,
    onProgressChanged: (Float) -> Unit
) {
    Slider(
        value = progress,
        onValueChange = onProgressChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
            ),
        colors = SliderDefaults.colors(
            thumbColor = MoyaColor.doosanPoint,
            activeTrackColor = MoyaColor.doosanPoint,
            inactiveTrackColor = MoyaColor.gray,
        ),
    )
}

@Composable
fun MusicPlayerBottomButtonView(
    onClickPlayButton: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(25.dp))
                .clickable { onClickPlayButton() },
            painter = painterResource(id = R.drawable.baseline_play_circle_24),
            contentDescription = null
        )
    }
}

@Composable
@Preview
fun MusicPlayerScreenPreview() {
    MusicPlayerScreen()
}
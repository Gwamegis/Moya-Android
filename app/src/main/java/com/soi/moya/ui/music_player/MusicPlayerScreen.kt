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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.models.Music
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun MusicPlayerScreen(
    viewModel: MusicPlayerViewModel
) {

    val currentPosition by rememberUpdatedState(newValue = viewModel.currentPosition.value)
    val duration = viewModel.getDuration()

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

    val file = R.raw.test
    val progress = remember { mutableFloatStateOf(0f) }
    val isLike = rememberSaveable { mutableStateOf( true ) }

    Column(
        modifier = Modifier
            .background(MoyaColor.doosanSub)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        MusicNavigationBar(
            music = music,
            isLike = isLike,
        ) {
            viewModel.updateLikeMusic(isLike = it)
            isLike.value = !it
        }

        MusicLylicView(
            music = music,
            modifier = Modifier.weight(1f)
        )

        MusicPlayerSlider(
            currentPosition = currentPosition ?: 0,
            duration = duration,
            viewModel = viewModel,
            onProgressChanged = { newProgress ->
                progress.value = newProgress
            }
        )

        MusicPlayerBottomButtonView(
            isPlaying = viewModel.isPlaying.value,
            onClickPlayButton = {
                viewModel.togglePlayPause()
            }
        )
    }
}

@Composable
fun MusicNavigationBar(
    music: Music,
    isLike: MutableState<Boolean>,
    onClickHeartButton: (Boolean) -> Unit
) {
    val likeIcon = if (isLike.value) {
        R.drawable.heart_fill
    } else {
        R.drawable.heart
    }

    val tintColor = if (isLike.value) {
        MoyaColor.doosanPoint
    } else {
        MoyaColor.white
    }

    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        IconButton(
            modifier = Modifier
                .size(20.dp),
            onClick = {
                onClickHeartButton(isLike.value)
            }) {

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
                tint = tintColor
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
    val gradientTopColor = scrollState.value > 10
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
    currentPosition: Int,
    duration: Int,
    viewModel: MusicPlayerViewModel,
    onProgressChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp
            )
    ) {
        Slider(
            value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
            onValueChange = {
                onProgressChanged(it)
                viewModel.seekTo((it * duration).toInt())
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MoyaColor.doosanPoint,
                activeTrackColor = MoyaColor.doosanPoint,
                inactiveTrackColor = MoyaColor.gray,
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = viewModel.formatTime(currentPosition),
                color = MoyaColor.white
            )
            Text(
                text = viewModel.formatTime(duration),
                color = MoyaColor.white
            )
        }
    }
}

@Composable
fun MusicPlayerBottomButtonView(
    isPlaying: Boolean,
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
            painter = painterResource(
                id = if (isPlaying) R.drawable.baseline_pause_circle_24 else {
                    R.drawable.baseline_play_circle_24
                }
            ),
            contentDescription = null
        )
    }
}

//@Composable
//@Preview
//fun MusicPlayerScreenPreview() {
//    val context = LocalContext.current.applicationContext
//    MusicPlayerScreen(viewModel = MusicPlayerViewModel(context = context))
//}
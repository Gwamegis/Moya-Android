package com.soi.moya.ui.music_player

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MusicPlayerScreen(
    viewModel: MusicPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    music: MusicInfo,
    modifier: Modifier = Modifier,
) {
    val currentPosition by rememberUpdatedState(newValue = viewModel.currentPosition.value)
    val duration = viewModel.getDuration()

    val progress = remember { mutableFloatStateOf(0f) }
    val isLike by viewModel.isLike.collectAsState()

    BackHandler {
        viewModel.popBackStack(navController)
    }

    music?.team?.let {
        Modifier
            .background(it.getSubColor())
            .fillMaxSize()
            .padding(20.dp)
    }?.let {
        Column(
        modifier = modifier.then(it)
    ) {
        music?.let { music ->
            MusicNavigationBar(
                music = music,
                isLike = isLike,
                onClickBackButton = {

                }
            ) {
                viewModel.updateLikeMusic(music)
            }

            MusicLylicView(
                music = music,
                modifier = Modifier.weight(1f)
            )
        }

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
}

@Composable
fun MusicNavigationBar(
    music: MusicInfo,
    isLike: Boolean,
    onClickBackButton: () -> Unit,
    onClickHeartButton: (Boolean) -> Unit
) {
    val likeIcon = if (isLike) {
        R.drawable.heart_fill
    } else {
        R.drawable.heart
    }

    val tintColor = if (isLike) {
        music.team.getPointColor()
    } else {
        MoyaColor.white
    }

    Row(
        modifier = Modifier.statusBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier
                .size(20.dp),
            onClick = {
                onClickBackButton()
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
            painter = painterResource(id = if (music.type) music.team.getPlayerAlbumImageResourceId() else music.team.getTeamAlbumImageResourceId()),
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = music.title,
                style = getTextStyle(style = MoyaFont.CustomBodyBold),
                color = MoyaColor.white
            )
            if (music.info.isNotEmpty()) {
                Text(
                    text = music.info,
                    style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
                    color = MoyaColor.gray
                )
            }

        }

        IconButton(
            modifier = Modifier
                .size(26.dp),
            onClick = {
                //TODO: 데이터 추가
                onClickHeartButton(isLike)
            }) {
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
    music: MusicInfo,
) {
    val scrollState = rememberScrollState()
    val gradientTopColor = scrollState.value > 10
    val gradientBottomColor = scrollState.value < scrollState.maxValue

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        Text(
            text = music.lyrics.replace("\\n", "\n"),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
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
                thumbColor = viewModel.team.getPointColor(),
                activeTrackColor = viewModel.team.getPointColor(),
                inactiveTrackColor = MoyaColor.gray,
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = viewModel.formatTime(currentPosition),
                style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
                color = MoyaColor.white
            )
            Text(
                text = viewModel.formatTime(duration),
                style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
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
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .size(60.dp)
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
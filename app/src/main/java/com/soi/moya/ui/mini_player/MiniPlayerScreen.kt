package com.soi.moya.ui.mini_player

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.music_player.MusicPlayerScreen
import com.soi.moya.ui.music_player.MusicPlayerViewModel
import com.soi.moya.ui.music_player.PlaylistScreen
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch
import kotlin.math.max

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MiniPlayerScreen(
    viewModel: MiniPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    maxHeight: Float,
    navController: NavHostController,
    music: MusicInfo
) {
    val height = remember { Animatable(viewModel.minHeight) }
    val coroutineScope = rememberCoroutineScope()
    val heightFraction = ((height.value - viewModel.minHeight) / (maxHeight - viewModel.minHeight)).coerceIn(0f, 1f)

    viewModel.setMaxHeight(maxHeight)

    val isMiniActivated by viewModel.isMiniPlayerActivated.collectAsState()

//    LaunchedEffect(isMiniActivated) {
//        if (!isMiniActivated) {
//            height.animateTo(maxHeight)
//        }
//        Log.d("MiniPlayer", isMiniActivated.toString())
//    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            Modifier
                .padding(
                    bottom = max(
                        0f,
                        (viewModel.bottomPadding - ((height.value - viewModel.minHeight) / (maxHeight - viewModel.minHeight)) * viewModel.bottomPadding)
                    ).dp
                )
                .padding(
                    horizontal = max(
                        0f,
                        (viewModel.horizontalPadding - ((height.value - viewModel.minHeight) / (maxHeight - viewModel.minHeight)) * viewModel.horizontalPadding)
                    ).dp
                )
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(height.value.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (height.value == maxHeight) 0.dp else 12.dp,
                        topEnd = if (height.value == maxHeight) 0.dp else 12.dp,
                        bottomStart = if (height.value == maxHeight) 0.dp else 12.dp,
                        bottomEnd = if (height.value == maxHeight) 0.dp else 12.dp
                    )
                )
                .background(color = music.team.getSubColor())
                .pointerInput(Unit) {
                    var dragStarted = false
                    var dragDirection = 0f
                    detectVerticalDragGestures(
                        onDragStart = { dragStarted = true },
                        onVerticalDrag = { change, dragAmount ->
                            if (dragStarted) {
                                dragDirection = dragAmount
                                dragStarted = false
                            }
                            val newHeight =
                                max(
                                    viewModel.minHeight,
                                    height.value - dragAmount * viewModel.scalingFactor
                                )
                            coroutineScope.launch {
                                height.snapTo(newHeight)
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            val targetHeight = when {
                                dragDirection < 0 && height.value > viewModel.threshold.value -> maxHeight
                                dragDirection > 0 && height.value < viewModel.threshold.value -> viewModel.minHeight
                                dragDirection < 0 && height.value <= viewModel.threshold.value -> viewModel.minHeight
                                dragDirection > 0 && height.value >= viewModel.threshold.value -> maxHeight
                                else -> height.value
                            }
                            val isMiniActivated = when {
                                dragDirection < 0 && height.value > viewModel.threshold.value -> false
                                dragDirection > 0 && height.value < viewModel.threshold.value -> true
                                dragDirection < 0 && height.value <= viewModel.threshold.value -> true
                                dragDirection > 0 && height.value >= viewModel.threshold.value -> false
                                else -> true
                            }
                            viewModel.setIsMiniplayerActivated(isMiniActivated)
                            coroutineScope.launch {
                                height.animateTo(targetHeight)
                            }
                        }
                    )
                }
                .then(
                    if (height.value == viewModel.minHeight)
                        Modifier
                            .clickable {
                                coroutineScope.launch {
                                    height.animateTo(maxHeight)
                                }
                                viewModel.setIsMiniplayerActivated(false)
                            }
                    else
                        Modifier
                )
        ) {

            if (heightFraction > 0.1f) {
                MusicPlayerScreen(
                    navController = navController,
                    music = music,
                    modifier = Modifier
                        .alpha(heightFraction)
                        .pointerInput(Unit) {
                            if (heightFraction <= 0f) {
                                detectTapGestures(onPress = { })
                            }
                        },
                    onClickBackButton = {
                        coroutineScope.launch {
                            height.animateTo(viewModel.minHeight)
                        }
                        viewModel.setIsMiniplayerActivated(true)
                    }
                )
            } else {
                MiniPlayer(
                    music = music,
                    modifier = Modifier
                        .alpha(1f - heightFraction)
                        .pointerInput(Unit) {
                            if (1f - heightFraction <= 0f) {
                                detectTapGestures(onPress = { })
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun MiniPlayer(
    viewModel: MusicPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    music: MusicInfo,
    modifier: Modifier = Modifier,
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = music.title,
                color = MoyaColor.background,
                style = getTextStyle(style = MoyaFont.CustomBodyBold),
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = music.team.getKrTeamName(),
                color = MoyaColor.gray.copy(alpha = 0.6f),
                style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if (isPlaying) R.drawable.pause_fill else {R.drawable.play_arrow }),
                contentDescription = "play/Pause",
                modifier = Modifier
                    .clickable { viewModel.togglePlayPause() }
                    .height(20.dp)
            )
            Image(
                painter = painterResource(R.drawable.play_next),
                contentDescription = "play next",
                modifier = Modifier.clickable { viewModel.playNextSong(1) })
        }
    }
}
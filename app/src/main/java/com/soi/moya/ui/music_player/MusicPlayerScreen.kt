package com.soi.moya.ui.music_player

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MusicPlayerScreen(
    navController: NavHostController,
    music: MusicInfo,
    modifier: Modifier = Modifier,
    onClickBackButton: () -> Unit
) {
    val viewModel: MusicPlayerViewModel = hiltViewModel()

    val currentPosition by rememberUpdatedState(newValue = viewModel.currentPosition.value)
    val duration = viewModel.getDuration()

    val progress = remember { mutableFloatStateOf(0f) }
    val isLike by viewModel.isLike.collectAsState()
    val isLyricDisplaying by viewModel.isLyricDisplaying.collectAsState()

    music?.team?.let {
        Modifier
            .background(it.getSubColor())
            .fillMaxSize()
    }?.let {
        Column(
            modifier = modifier.then(it)
    ) {
        music?.let { music ->
            MusicNavigationBar(
                music = music,
                isLike = isLike,
                onClickBackButton = {onClickBackButton()}
            ) {
                viewModel.updateLikeMusic(music)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                if (isLyricDisplaying) {
                    PlaylistScreen(
                        music = music
                    )
                } else {
                    MusicLyricScreen(music = music)
                }
                Box(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(43.dp)
                        .background(
                            color = music.team.getBackgroundColor(),
                            shape = CircleShape
                        )
                        .align(Alignment.BottomEnd) // 오른쪽 하단에 배치
                        .clickable {
                            viewModel.toggleisLyricDisplaying()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.playlist), contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

        }

        MusicPlayerSlider(
            music = music,
            currentPosition = currentPosition ?: 0,
            duration = duration,
            viewModel = viewModel
        ) { newProgress ->
            progress.value = newProgress
        }

            MusicPlayerBottomButtonView(
            isPlaying = viewModel.isPlaying.value,
            onClickPlayButton = {
                viewModel.togglePlayPause()
            },
            onClickSkipButton = {increment ->
                viewModel.playNextSong(increment)
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
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier
                .size(15.dp),
            onClick = {
                onClickBackButton()
            }) {

            Icon(
                painterResource(id = R.drawable.chevron_down),
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
fun MusicPlayerSlider(
    music: MusicInfo,
    currentPosition: Long,
    duration: Long,
    viewModel: MusicPlayerViewModel,
    onProgressChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 40.dp,
                end = 40.dp
            )
    ) {
        Slider(
            value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
            onValueChange = {
                onProgressChanged(it)
                viewModel.seekTo((it * duration).toLong())
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = music.team.getPointColor(),
                activeTrackColor = music.team.getPointColor(),
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
    onClickPlayButton: () -> Unit,
    onClickSkipButton: (increment: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(54.dp, Alignment.CenterHorizontally),
    ) {
        Image(
            modifier = Modifier
                .size(25.dp)
                .aspectRatio(1f)
                .clickable { onClickSkipButton(-1) },
            painter = painterResource(
                id = R.drawable.skip_previous
            ),
            contentDescription = "skip previous"
        )

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

        Image(
            modifier = Modifier
                .size(25.dp)
                .aspectRatio(1f)
                .clickable { onClickSkipButton(1) },
            painter = painterResource(
                id = R.drawable.skip_next
            ),
            contentDescription = "skip next"
        )
    }
}
package com.soi.moya.ui.music_player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    music: MusicInfo,
    viewModel: PlaylistViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val defaultPlaylists by viewModel.defaultPlaylist.collectAsState()
    val currentSongId by viewModel.currentSongId.observeAsState()
    val scrollState = rememberLazyListState()
    val gradientTopColor = scrollState.canScrollBackward
    val gradientBottomColor = scrollState.canScrollForward

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(
                items = defaultPlaylists.itemList,
                key = { item -> item.songId }
            ) { item ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart) {
                            viewModel.deletePlaylistItem(song = item)
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismiss(
                    modifier = Modifier.animateItemPlacement(),
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {}
                ) {
                    PlaylistItem(
                        viewModel,
                        music = item,
                        isCurrentSong = item.songId == currentSongId
                    )
                }
            }
            
            item { 
                Spacer(modifier = Modifier.height(73.dp))
            }
        }
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
fun PlaylistItem(
    viewModel: PlaylistViewModel,
    music: StoredMusic,
    isCurrentSong: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 40.dp)
            .clickable { viewModel.onTapListItem(music) }
    ) {
        if (isCurrentSong) {
            AnimationLoader(
                viewModel = viewModel
            )
        } else {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp)),
                painter = painterResource(
                    id = if (music.type) Team.valueOf(music.team).getPlayerAlbumImageResourceId()
                    else Team.valueOf(music.team).getTeamAlbumImageResourceId()
                ),
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = music.title,
                style = getTextStyle(style = MoyaFont.CustomBodyMedium),
                color = MoyaColor.white
            )
            Text(
                text = Team.valueOf(music.team).getKrTeamName(),
                style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
                color = MoyaColor.gray
            )
        }

        /* TODO: 음악 순서 변경 기능 추가
        Image(
            painter = painterResource(id = R.drawable.playlistitem),
            contentDescription = "",
            modifier = Modifier
                .size(18.dp)
                .alpha(0.5f),
            colorFilter = ColorFilter.tint(MoyaColor.gray)
        )
         */
    }
}
@Composable
fun AnimationLoader(
    viewModel: PlaylistViewModel
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.waveform))
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { if (isPlaying) progress else 1f },
        modifier = Modifier.size(40.dp)
    )
}
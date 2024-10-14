package com.soi.moya.ui.music_player

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    music: MusicInfo,
) {
    val viewModel: PlaylistViewModel = hiltViewModel()

//    val defaultPlaylists by viewModel.defaultPlaylists.collectAsState()
    val mediaItemLists by viewModel.mediaItemList.collectAsState()

    val currentSongId by viewModel.currentSongId.observeAsState()
    val currentSongPosition by viewModel.currentSongPosition.observeAsState()
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
                items = mediaItemLists,
                key = { item -> item.mediaId }
            ) { item ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart) {
                            Log.e("*** playlist screen", "dismissValue order: $item")
                            viewModel.deletePlaylistItem(songId = item.mediaId, order = viewModel.getMediaItemIndex(mediaId = item.mediaId))
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismiss(
                    modifier = Modifier
                        .animateItemPlacement()
                        .clickable { viewModel.onTapListItem(item) },
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {}
                ) {
                    PlaylistItem(
                        viewModel,
                        music = item,
                        isCurrentSong = viewModel.getMediaItemIndex(item.mediaId) == currentSongPosition
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
                music.team.getSubColor().copy(0.7f),
                Color.Transparent
            )
        )

        GradientBox(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            isVisible = gradientBottomColor,
            gradientColors = listOf(
                Color.Transparent,
                music.team.getSubColor().copy(0.7f),
                music.team.getSubColor()
            )
        )
    }
}
@Composable
fun PlaylistItem(
    viewModel: PlaylistViewModel,
    music: MediaItem,
    isCurrentSong: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = if (isCurrentSong) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .padding(vertical = 10.dp, horizontal = 40.dp)
    ) {
        if (isCurrentSong) {
            AnimationLoader(
                viewModel = viewModel
            )
        } else {
            MediaItemImage(artworkUri = music.mediaMetadata.artworkUri)
        }

        Column(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = music.mediaMetadata.title.toString(),
                style = getTextStyle(style = MoyaFont.CustomBodyMedium),
                color = MoyaColor.white
            )
            Text(
                text = music.mediaMetadata.artist.toString(),
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
fun MediaItemImage(artworkUri: Uri?) {
    val painter = rememberImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(artworkUri) // MediaItem의 artworkUri를 데이터로 사용
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop // 이미지 크롭
    )
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
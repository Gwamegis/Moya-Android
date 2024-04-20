package com.soi.moya.ui.mini_player

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.soi.moya.R
import com.soi.moya.models.Team
import com.soi.moya.ui.music_player.MusicPlayerScreen
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun MiniPlayerScreen(
    maxHeight: Float,
    navController: NavHostController,
) {
    val minHeight = 55f
    val height = remember { Animatable(minHeight) } // Animatable을 사용하여 높이를 관리
    val coroutineScope = rememberCoroutineScope()
    val threshold = maxHeight / 2 // 임계값 설정
    val scalingFactor = 0.7f
    val bottomPadding = 100f
    val horizontalPadding = 10f

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
                        (bottomPadding - ((height.value - minHeight) / (maxHeight - minHeight)) * bottomPadding)
                    ).dp
                )
                .padding(
                    horizontal = max(
                        0f,
                        (horizontalPadding - ((height.value - minHeight) / (maxHeight - minHeight)) * horizontalPadding)
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
                .background(color = Team.lotte.getSubColor())
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
                                max(minHeight, height.value - dragAmount * scalingFactor)
                            coroutineScope.launch { height.snapTo(newHeight) }
                            change.consume()
                        },
                        onDragEnd = {
                            val targetHeight = when {
                                dragDirection < 0 && height.value > threshold -> maxHeight
                                dragDirection > 0 && height.value < threshold -> minHeight
                                dragDirection < 0 && height.value <= threshold -> minHeight
                                dragDirection > 0 && height.value >= threshold -> maxHeight
                                else -> height.value
                            }
                            coroutineScope.launch {
                                height.animateTo(targetHeight)
                            }
                        }
                    )
                }
                .then(
                    if (height.value == minHeight)
                        Modifier
                            .clickable {
                                coroutineScope.launch {
                                    height.animateTo(maxHeight)
                                }
                            }
                    else
                        Modifier
                )
        ) {
            if (height.value == maxHeight) {
                MusicPlayerScreen(
                    navController = navController
                )
            } else {
                MiniPlayer()
            }
        }
    }
}

@Composable
fun MiniPlayer() {
    Row(
        modifier = Modifier
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
                text = "힘차게 외쳐보자",
                color = MoyaColor.background,
                style = getTextStyle(style = MoyaFont.CustomBodyBold),
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "롯데 자이언츠",
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
                painter = painterResource(R.drawable.play_arrow),
                contentDescription = "play/Pause",
                modifier = Modifier.clickable { Log.d("test", "test") }
            )
            Image(
                painter = painterResource(R.drawable.play_next),
                contentDescription = "play next",
                modifier = Modifier.clickable {  })
        }
    }
}
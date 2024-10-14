package com.soi.moya.base

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.soi.moya.playback.PlaybackService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseComposeActivity: ComponentActivity() {

    @Composable
    abstract fun Content()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        setContent {
            Content()
            val intent = Intent(this, PlaybackService::class.java)
            startService(intent)
        }
    }
}
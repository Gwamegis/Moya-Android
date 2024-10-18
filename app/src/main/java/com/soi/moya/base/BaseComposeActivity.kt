package com.soi.moya.base

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.ProcessLifecycleOwner
import com.soi.moya.data.AppLifecycleObserver
import com.soi.moya.models.UserPreferences
import com.soi.moya.playback.PlaybackService
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseComposeActivity: ComponentActivity() {

    @Inject
    lateinit var musicStateRepository: MusicStateRepository

    @Composable
    abstract fun Content()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(musicStateRepository, this))

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
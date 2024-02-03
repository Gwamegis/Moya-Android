package com.soi.moya.repository

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.soi.moya.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicPlayerManager(application: Application) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val mediaPlayer = MediaPlayer.create(application, R.raw.test)

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            play()
        } else {
            pause()
        }
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun stop() {
        if (mediaPlayer.isPlaying) {
            // prepare를 다시 호출하지 않기 위해 pause 및 seekto 사용
            pause()
            seekTo(0)
        }
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

}
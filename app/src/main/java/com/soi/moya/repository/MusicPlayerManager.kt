package com.soi.moya.repository

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MusicPlayerManager(application: Application) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
    }

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

    fun playMusicFromUrl(url: String) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(url)
                prepare()
                play()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
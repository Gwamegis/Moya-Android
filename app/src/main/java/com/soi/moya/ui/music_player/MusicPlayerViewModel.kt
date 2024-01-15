package com.soi.moya.ui.music_player

import android.app.Application
import android.content.Context
import android.content.res.AssetFileDescriptor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.domain.MusicPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

class MusicPlayerViewModel(context: Context): ViewModel() {
    private val _musicPlayer: MusicPlayerManager = MusicPlayerManager(context = context)

    private val _music = MutableLiveData<File>()
    val music: LiveData<File> get() = _music

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _duration = MutableLiveData<Int>()
    val duration: LiveData<Int> get() = _duration

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    // 추가된 부분
//    val progress: LiveData<Float>
//        get() = Transformations.map(_currentPosition) { position ->
//            if (_duration.value != null && _duration.value!! > 0) {
//                position.toFloat() / _duration.value!!
//            } else {
//                0f
//            }
//        }

    init {
        _isPlaying.value = _musicPlayer.isPlaying()
        _duration.value = _musicPlayer.getMusicDuration()
        startUpdateCurrentPosition()
    }

    private fun startUpdateCurrentPosition() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = _musicPlayer.getCurrentPosition()
                delay(1000)
            }
        }
    }

    fun setMusicFile(file: File) {
        _music.value = file
    }

    fun playMusic(file: AssetFileDescriptor) {
        _music.value?.let { playedFile ->
            if (_musicPlayer.isPlaying()) {
                _musicPlayer.stopMusic()
            }
            _musicPlayer.playMusic()
        }
    }

    fun togglePlayPause() {
        if (_musicPlayer.isPlaying()) {
            _musicPlayer.stopMusic()
        } else {
            _musicPlayer.playMusic()
        }
    }

    fun checkIsPlayingMusic(): Boolean {
        return _musicPlayer.isPlaying()
    }

    fun updateLikeMusic(isLike: Boolean) {
        if (isLike) {
            unlikeMusic()
        } else {
            likeMusic()
        }
    }

    fun getCurrentTime(): String {
        return formatTime(_currentPosition.value ?: 0)
    }

    fun getEndTime(): String {
        return formatTime(_duration.value ?: 0)
    }

    fun formatTime(time: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun likeMusic() {

    }

    private fun unlikeMusic() {

    }
}


package com.soi.moya.ui.music_player

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.soi.moya.repository.MusicPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MusicPlayerViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    private val _musicPlayerManager = mutableStateOf(MusicPlayerManager(application))

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val musicPlayerManager: State<MusicPlayerManager>
        get() = _musicPlayerManager

    val currentPosition: State<Int> get() = _currentPosition
    private val _currentPosition = mutableIntStateOf(0)

    init {
        _musicPlayerManager.value.play()
        startUpdateCurrentPositionAndDuration()
    }

    companion object {
        val Factory: ViewModelProvider.Factory= object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelCalss: Class<T>,
                extras: CreationExtras
            ) : T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()

                return MusicPlayerViewModel(
                    application,
                    savedStateHandle
                ) as T
            }
        }
    }

    private fun startUpdateCurrentPositionAndDuration() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = musicPlayerManager.value.getCurrentPosition()
                _isPlaying.value = musicPlayerManager.value.isPlaying()
                delay(1000)
            }
        }
    }

    fun togglePlayPause() {
        musicPlayerManager.value.togglePlayPause()
    }

    fun updateLikeMusic(isLike: Boolean) {
        if (isLike) {
            unlikeMusic()
        } else {
            likeMusic()
        }
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

    fun getDuration(): Int {
        return musicPlayerManager.value.getDuration()
    }

    fun seekTo(position: Int) {
        musicPlayerManager.value.seekTo(position)
    }

    fun popBackStack(navController: NavHostController) {
        _musicPlayerManager.value.stop()
        navController.popBackStack()
    }
}
package com.soi.moya.ui.music_player

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.ui.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val storedMusicRepository: StoredMusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    private val musicPlaybackManager: MusicPlaybackManager
): ViewModel() {
    private val _userPreferences = UserPreferences(application)

    private val _currentSongId = MutableLiveData<String?>()
    var music: StoredMusic? = null

    private val _songId: String
        get() = _currentSongId.value ?: ""

    private val _teamName: String = savedStateHandle["team"] ?: "doosan"
    val team: Team = Team.valueOf(_teamName)

//    private val _musicPlayerManager = mutableStateOf(
//        MusicPlayerManager.getInstance(
//            application = application,
//            storedMusicRepository = storedMusicRepository
//            )
//    )
//    private val musicPlayerManager: State<MusicPlayerManager>
//        get() = _musicPlayerManager

//    private lateinit var mediaControllerManager: MediaControllerManager
//    private lateinit var musicPlaybackManager: MusicPlaybackManager

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> = _isLike

    val currentPosition: State<Long> get() = _currentPosition
    private val _currentPosition = mutableLongStateOf(0)

    private val _isLyricDisplaying = MutableStateFlow(true)
    val isLyricDisplaying: StateFlow<Boolean> = _isLyricDisplaying

    init {
//        mediaControllerManager = MediaControllerManager(application)
//        musicPlaybackManager = MusicPlaybackManager(mediaControllerManager, application)

        subscribeIsLyricView()
        startUpdateCurrentPositionAndDuration()
        subscribeCurrentSongID()
    }
    private fun startUpdateCurrentPositionAndDuration() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = musicPlaybackManager.getCurrentPosition()
                _isPlaying.value = mediaControllerManager.controller?.isPlaying ?: false
//                _currentPosition.value = musicPlayerManager.value.getCurrentPosition()
//                _isPlaying.value = musicPlayerManager.value.isPlaying.value
                delay(1000)
            }
        }
    }

    private fun subscribeCurrentSongID() {
        viewModelScope.launch {
            _userPreferences.currentPlaySongId.collect { songId ->
                _currentSongId.postValue(songId)
                if (songId != null) {
                    val liked = storedMusicRepository.isSongLiked(songId)
                    _isLike.value = liked
                }
            }
        }
    }

    private fun subscribeIsLyricView() {
        viewModelScope.launch {
            _userPreferences.isLyricDisplaying.collect { value ->
                _isLyricDisplaying.value = value
            }
        }
    }

    fun toggleisLyricDisplaying() {
        viewModelScope.launch {
            _userPreferences.saveIsLyricState(!_isLyricDisplaying.value)
        }
    }

    fun togglePlayPause() {
        musicPlaybackManager.togglePlayPause()
//        musicPlayerManager.value.togglePlayPause()
    }

    fun playNextSong(increment: Int) {
        musicPlaybackManager.playNextSong(increment)
//        musicPlayerManager.value.playNextSong(increment)
    }

    fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getDuration(): Long {
        return musicPlaybackManager.getDuration()
//        return musicPlayerManager.value.getDuration()
    }

    fun seekTo(position: Long) {
        musicPlaybackManager.seekTo(position)
//        musicPlayerManager.value.seekTo(position)
    }

    //좋아요 관련 함수
    fun updateLikeMusic(music: MusicInfo) {
        val newLikeStatus = !isLike.value
        _isLike.value = newLikeStatus

        if (newLikeStatus) {
            likeMusic(music)
        } else {
            unlikeMusic(music)
        }
    }

    private fun likeMusic(music: MusicInfo) {
        viewModelScope.launch {
            val order = storedMusicRepository.getItemCount(playlist = "favorite")
            val item = music.toStoredMusic(
                team = music.team,
                order = order,
                date = Utility.getCurrentTimeString(),
                playlist = "favorite"
            )

            withContext(Dispatchers.IO) {
                storedMusicRepository.insertItem(item.toItem())
            }
        }
    }

    private fun unlikeMusic(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.deleteById(id = music.id, playlist = "favorite")
            }
        }
    }
}
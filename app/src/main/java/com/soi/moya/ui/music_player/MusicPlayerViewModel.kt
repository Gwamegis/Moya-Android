package com.soi.moya.ui.music_player

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
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
    savedStateHandle: SavedStateHandle,
    private val storedMusicRepository: StoredMusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val musicStateRepository: MusicStateRepository
): ViewModel() {

    val currentSongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()
    val isLyricDisplaying: LiveData<Boolean> = musicStateRepository.isLyricDisplaying.asLiveData()

    var music: StoredMusic? = null

    private val _teamName: String = savedStateHandle["team"] ?: "doosan"
    val team: Team = Team.valueOf(_teamName)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> = _isLike

    val currentPosition: State<Long> get() = _currentPosition
    private val _currentPosition = mutableLongStateOf(0)


    init {
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
        currentSongId.observeForever { songId ->
            if (songId != null) {
                viewModelScope.launch {
                    val liked = storedMusicRepository.isSongLiked(songId)
                    _isLike.value = liked
                }
            }
        }
    }

    fun toggleIsLyricDisplaying() {
        val currentValue = isLyricDisplaying.value ?: false
        musicStateRepository.setLyricDisplaying(!currentValue)
    }

    fun togglePlayPause() {
        musicPlaybackManager.togglePlayPause()
    }

    fun playNextSong(increment: Int) {
        musicPlaybackManager.playNextSong(increment)
    }

    fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getDuration(): Long {
        return musicPlaybackManager.getDuration()
    }

    fun seekTo(position: Long) {
        musicPlaybackManager.seekTo(position)
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
package com.soi.moya.ui.music_player

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.UserPreferences
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _currentSongId = MutableLiveData<String?>()
    val currentSongId: LiveData<String?> = _currentSongId

    private val _currentSongPosition = MutableLiveData<Int?>()
    val currentSongPosition: LiveData<Int?> = _currentSongPosition

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    val mediaItemList: StateFlow<List<MediaItem>> = mediaControllerManager.mediaItemList

    init {
        observeCurrentSongId()
        observeCurrentSongPosition()
        collectMusicIsPlaying()
    }

    private fun observeCurrentSongId() {
        viewModelScope.launch {
            userPreferences.currentPlaySongId.collect{
                _currentSongId.value = it
            }
        }
    }

    private fun observeCurrentSongPosition() {
        viewModelScope.launch {
            userPreferences.currentPlaySongPosition.collect{
                _currentSongPosition.value = it
            }
        }
    }
    private fun collectMusicIsPlaying() {
        viewModelScope.launch {
            mediaControllerManager.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }
    }

    fun deletePlaylistItem(songId: String, order: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.deleteById(id = songId, playlist = "default")
                storedMusicRepository.updateOrder(start = order + 1, end = mediaItemList.value.count(), increment = -1)
            }
        }
    }

    fun onTapListItem(music: StoredMusic) {
        //TODO: currentSongId가 아닌 current position을 저장
        saveCurrentSongId(music.songId)
        playMusic(music = music)
    }

    fun onTapListItem(music: MediaItem) {
        //TODO: currentSongId가 아닌 current position을 저장
        saveCurrentSongId(music.mediaId)
        playMusic(music = music)
    }
    private fun saveCurrentSongId(songId: String) {
        viewModelScope.launch {
            userPreferences.saveCurrentSongId(songId)
            userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    private fun playMusic(music: StoredMusic) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }

    private fun playMusic(music: MediaItem) {
        viewModelScope.launch {
            musicPlaybackManager.playMediaItemById(music.mediaId)
        }
    }

    fun getMediaItemIndex(mediaId: String): Int {
        return mediaItemList.value.indexOfFirst { it.mediaId == mediaId }
    }

    fun getMediaItemIndexById(mediaId: String): Int? {
        return mediaItemList.value.indexOfFirst { it.mediaId == mediaId }.takeIf { it >= 0 }
    }
}
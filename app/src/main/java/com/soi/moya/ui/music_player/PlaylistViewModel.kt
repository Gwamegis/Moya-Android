package com.soi.moya.ui.music_player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.UserPreferences
import com.soi.moya.repository.MusicPlayerManager
import com.soi.moya.ui.music_storage.MusicStorageViewModel
import com.soi.moya.ui.music_storage.StorageUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    application: Application,
    private val storedMusicRepository: StoredMusicRepository
) : AndroidViewModel(application) {
    private val _userPreferences = UserPreferences(application)

    private val musicPlayerManager = MusicPlayerManager.getInstance(
        application = application,
        storedMusicRepository
    )

    private val _currentSongId = MutableLiveData<String?>()
    val currentSongId: LiveData<String?> = _currentSongId

    private val _currentSongPosition = MutableLiveData<Int?>()
    val currentSongPosition: LiveData<Int?> = _currentSongPosition

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

//    val defaultPlaylist: StateFlow<StorageUiState> =
//        storedMusicRepository.getByDefaultPlaylist().map { StorageUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(MusicStorageViewModel.TIMEOUT_MILLIS),
//                initialValue = StorageUiState()
//            )

    // StateFlow to observe media item list
    val mediaItemList: StateFlow<List<MediaItem>> = musicPlayerManager.mediaItemList

    init {
        observeCurrentSongId()
        observeCurrentSongPosition()
        collectMusicIsPlaying()
    }

    private fun observeCurrentSongId() {
        viewModelScope.launch {
            _userPreferences.currentPlaySongId.collect{
                _currentSongId.value = it
            }
        }
    }

    private fun observeCurrentSongPosition() {
        viewModelScope.launch {
            _userPreferences.currentPlaySongPosition.collect{
                _currentSongPosition.value = it
            }
        }
    }
    private fun collectMusicIsPlaying() {
        viewModelScope.launch {
            musicPlayerManager.isPlaying.collect { playing ->
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
            _userPreferences.saveCurrentSongId(songId)
            _userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    private fun playMusic(music: StoredMusic) {
        viewModelScope.launch {
            musicPlayerManager.playMusic(music)
        }
    }

    private fun playMusic(music: MediaItem) {
        viewModelScope.launch {
            musicPlayerManager.playMediaItemById(music.mediaId)
        }
    }

    fun getMediaItemIndex(mediaId: String): Int {
        return mediaItemList.value.indexOfFirst { it.mediaId == mediaId }
    }

    fun getMediaItemIndexById(mediaId: String): Int? {
        return mediaItemList.value.indexOfFirst { it.mediaId == mediaId }.takeIf { it >= 0 }
    }
}
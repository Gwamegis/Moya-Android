package com.soi.moya.ui.music_player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.UserPreferences
import com.soi.moya.ui.music_storage.MusicStorageViewModel
import com.soi.moya.ui.music_storage.StorageUiState
import kotlinx.coroutines.Dispatchers
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

    private val _currentSongId = MutableLiveData<String?>()
    val currentSongId: LiveData<String?> = _currentSongId

    val defaultPlaylist: StateFlow<StorageUiState> =
        storedMusicRepository.getByDefaultPlaylist().map { StorageUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MusicStorageViewModel.TIMEOUT_MILLIS),
                initialValue = StorageUiState()
            )

    init {
        observeCurrentSongId()
    }

    private fun observeCurrentSongId() {
        viewModelScope.launch {
            _userPreferences.currentPlaySongId.collect{
                _currentSongId.value = it
            }
        }
    }

    fun deletePlaylistItem(song: StoredMusic) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.deleteById(id = song.songId, playlist = "default")
                storedMusicRepository.updateOrder(start = song.order + 1, end = defaultPlaylist.value.itemList.count(), increment = -1)
            }
        }
    }
}
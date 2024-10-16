package com.soi.moya.ui.music_storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.toDefaultItem
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
import com.soi.moya.ui.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicStorageViewModel @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val musicStateRepository: MusicStateRepository
): ViewModel() {

    val currentSongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()

    val storageUiState: StateFlow<StorageUiState> =
        storedMusicRepository.getByStoragePlaylist().map { StorageUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = StorageUiState()
            )
    val startHeightNum = 200

    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: Map<String, LiveData<List<String>>> get() = _seasonSongManager.getSeasonSongs()

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

    fun fetchAlbumImageResourceId(music: MusicInfo, team: Team): Int {
        return if (seasonSongs[team.name]?.value?.contains(music.title) == true) {
            team.getSeasonSongAlbumImageResourceId()
        } else {
            if(music.type) {
                team.getPlayerAlbumImageResourceId()
            } else {
                team.getTeamImageResourceId()
            }
        }
    }

    fun onTapListItem(music: StoredMusic) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.songId, "default")

                if (existingMusic != null) {
                    storedMusicRepository.updateOrder(
                        start = 0,
                        end = existingMusic.order,
                        increment = 1
                    )
                    storedMusicRepository.updateOrder(existingMusic.songId, 0)
                } else {
                    val order = storedMusicRepository.getItemCount(playlist = "default")
                    storedMusicRepository.updateOrder(
                        start = 0,
                        end = order,
                        increment = 1
                    )
                    val newMusic = music.toDefaultItem(
                        playlist = "default",
                        order = 0,
                        date = Utility.getCurrentTimeString()
                    )
                    storedMusicRepository.insertItem(newMusic)
                }
                saveCurrentSongId(music.songId)
                if (currentSongId.value != music.songId) {
                    playMusic(music)
                }
            }
        }
    }
    private fun saveCurrentSongId(songId: String) {
        musicStateRepository.setCurrentPlaySongId(songId)
        musicStateRepository.setMiniPlayerActivated(false)
    }

    private fun playMusic(music: StoredMusic) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }
}

data class StorageUiState(val itemList: List<StoredMusic> = listOf())
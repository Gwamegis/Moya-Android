package com.soi.moya.ui.music_storage

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.toMediaItem
import com.soi.moya.repository.HandlePlaylistItemUseCase
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
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
    private val application: Application,
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val mediaControllerManager: MediaControllerManager,
    private val musicStateRepository: MusicStateRepository,
    private val handlePlaylistItemUseCase: HandlePlaylistItemUseCase,
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
        val mediaItem = music.toMediaItem(application.filesDir.absolutePath)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.songId, "default")
                val count = storedMusicRepository.getItemCount("default")

                val position: Int = if (existingMusic != null) {
                    handlePlaylistItemUseCase.handleExistingMusic(existingMusic, mediaItem, count)
                } else {
                    handlePlaylistItemUseCase.handleNewMusic(music, mediaItem, count)
                }

                withContext(Dispatchers.Main) {
                    mediaControllerManager.updateMediaController()
                }
                saveCurrentSongId(music.id, position)
                if (currentSongId.value != music.id) {
                    playMusic(music)
                }
            }
        }
    }
    private fun saveCurrentSongId(songId: String, position: Int) {
        musicStateRepository.setCurrentPlaySongId(songId)
        musicStateRepository.setMiniPlayerActivated(false)
        musicStateRepository.setCurrentPlaySongPosition(position)
    }

    private fun playMusic(music: StoredMusic) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }
}

data class StorageUiState(val itemList: List<StoredMusic> = listOf())
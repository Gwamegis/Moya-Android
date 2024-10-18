package com.soi.moya.ui.music_list

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.soi.moya.data.MusicManager
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.toMediaItem
import com.soi.moya.repository.HandlePlaylistItemUseCase
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val application: Application,
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val mediaControllerManager: MediaControllerManager,
    private val musicStateRepository: MusicStateRepository,
    private val handlePlaylistItemUseCase: HandlePlaylistItemUseCase
) : ViewModel() {
    val selectedTeam: LiveData<Team?> = musicStateRepository.selectedTeam.asLiveData()
    val currentSongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()

    private val _musicManager = MusicManager.getInstance()
    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: LiveData<List<String>> get() = _seasonSongManager.getSeasonSongsForTeam(selectedTeam.value?.name ?: "doosan")
    private val _teamMusics = mutableStateOf(emptyList<MusicInfo>())
    private val _playerMusics = mutableStateOf(emptyList<MusicInfo>())

    init {
        observeUserPreference()
    }

    private fun observeUserPreference() {
        selectedTeam.observeForever {
            updateMusicForSelectedTeam()
        }
    }

    fun onTapListItem(music: MusicInfo) {
        val mediaItem = music.toMediaItem(application.filesDir.absolutePath)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.id, "default")
                val count = storedMusicRepository.getItemCount("default")
                if (existingMusic != null) {
                    handlePlaylistItemUseCase.handleExistingMusic(mediaItem, count)
                } else {
                    handlePlaylistItemUseCase.handleNewMusic(music, mediaItem, count)
                }

                withContext(Dispatchers.Main) {
                    mediaControllerManager.updateMediaController()
                }

                saveCurrentSongId(music.id, 0)
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

    private fun updateMusicForSelectedTeam() {
        val selectedTeam = selectedTeam.value?.name ?: "doosan"
        _musicManager.getFilteredSelectedTeamMusic(selectedTeam).observeForever { musics ->
            _teamMusics.value = musics?.filter { !it.type } ?: emptyList()
            _playerMusics.value = musics?.filter { it.type } ?: emptyList()
        }
    }

    fun getMusicListSize(page: Int): Int {
        return when (page) {
            0 -> _teamMusics.value.size
            1 -> _playerMusics.value.size
            else -> 0
        }
    }

    fun getMusicAt(page: Int, index: Int): MusicInfo {
        return when (page) {
            0 -> _teamMusics.value[index]
            1 -> _playerMusics.value[index]
            else -> _teamMusics.value[index]
        }
    }

    fun fetchAlbumImageResourceId(music: MusicInfo, team: Team): Int {
        return if (seasonSongs.value?.contains(music.title) == true) {
            team.getSeasonSongAlbumImageResourceId()
        } else {
            if(music.type) {
                team.getPlayerAlbumImageResourceId()
            } else {
                team.getTeamImageResourceId()
            }
        }
    }

    private fun playMusic(music: MusicInfo) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }

    fun onTapSelectTeamButton() {
        musicStateRepository.setNeedHideMiniPlayer(true)
    }
}
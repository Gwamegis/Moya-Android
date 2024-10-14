package com.soi.moya.ui.music_list

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.soi.moya.data.MusicManager
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.ui.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _selectedTeam = MutableStateFlow<String>("doosan")
    val selectedTeam: StateFlow<String> = _selectedTeam
    private val _musicManager = MusicManager.getInstance()
    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: LiveData<List<String>> get() = _seasonSongManager.getSeasonSongsForTeam(_selectedTeam.value)
    private val _teamMusics = mutableStateOf(emptyList<MusicInfo>())
    private val _playerMusics = mutableStateOf(emptyList<MusicInfo>())

    init {
        observeUserPreference()
        observeSelectedTeam()
    }

    private fun observeUserPreference() {
        viewModelScope.launch {
            userPreferences.getSelectedTeam.collect { team ->
                _selectedTeam.value = team ?: "doosan"
                updateMusicForSelectedTeam()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onTapListItem(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.id, "default")
                val count = storedMusicRepository.getItemCount("default")

                if (existingMusic != null) {
                    // 이미 저장된 음악 정보가 있을 때
                    storedMusicRepository.updateOrder(
                        start = existingMusic.order+1,
                        end = count-1,
                        increment = -1
                    )
                    storedMusicRepository.updateOrder(existingMusic.songId, count-1)
                } else {
                    // 새로운 음악 정보를 추가할 때
                    val order = storedMusicRepository.getItemCount(playlist = "default")

                    val newMusic = music.toStoredMusic(
                        team = music.team,
                        order = count-1,
                        date = Utility.getCurrentTimeString(),
                        playlist = "default"
                    )
                    storedMusicRepository.insertItem(newMusic.toItem())
                }

                saveCurrentSongId(music.id, count-1)
                saveIsMiniplayerActivated()

                val currentSongId = userPreferences.currentPlaySongId.firstOrNull()

                if (currentSongId != music.id) {
                    playMusic(music)
                }
            }
        }
    }

    private fun saveIsMiniplayerActivated() {
        viewModelScope.launch {
            userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    private fun saveCurrentSongId(songId: String, position: Int) {
        viewModelScope.launch {
            userPreferences.saveCurrentSongId(songId)
            userPreferences.saveIsMiniplayerActivated(false)
            userPreferences.saveCurrentSongPosition(position)
        }
    }

    private fun updateMusicForSelectedTeam() {

        val selectedTeam = _selectedTeam.value
        _musicManager.getFilteredSelectedTeamMusic(selectedTeam).observeForever { musics ->
            _teamMusics.value = musics?.filter { !it.type } ?: emptyList()
            _playerMusics.value = musics?.filter { it.type } ?: emptyList()
        }
    }

    private fun observeSelectedTeam() {
        viewModelScope.launch {
            userPreferences.getSelectedTeam.collect { team ->
                _selectedTeam.value = team ?: "doosan"
                updateMusicForSelectedTeam()
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(music: MusicInfo) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
//            musicPlayerManager.value.playMusic(music)
        }
    }

    fun onTapSelectTeamButton() {
        viewModelScope.launch {
            userPreferences.saveIsNeedHideMiniPlayer(isNeedToHide = true)
        }
    }
}
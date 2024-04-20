package com.soi.moya.ui.music_list

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.soi.moya.data.MusicManager
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicListViewModel(
    // TODO: Repository 연결
    application: Application
) : AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)
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

    fun onSongSelected(music: MusicInfo) {
        viewModelScope.launch {
            _userPreferences.showingMiniPlayer()
            _userPreferences.setSongId(music = music)
        }
    }

    private fun observeUserPreference() {
        viewModelScope.launch {
            _userPreferences.getSelectedTeam.collect { team ->
                _selectedTeam.value = team ?: "doosan"
                updateMusicForSelectedTeam()
            }
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
            _userPreferences.getSelectedTeam.collect { team ->
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
}
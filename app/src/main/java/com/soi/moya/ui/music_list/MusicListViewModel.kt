package com.soi.moya.ui.music_list

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.soi.moya.data.MusicManager
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.Music
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
    private val _teamMusics = mutableStateOf(emptyList<Music>())
    private val _playerMusics = mutableStateOf(emptyList<Music>())

    init {
        observeUserPreference()
        observeSelectedTeam()
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

    fun getMusicAt(page: Int, index: Int): Music {
        return when (page) {
            0 -> _teamMusics.value[index]
            1 -> _playerMusics.value[index]
            else -> _teamMusics.value[index]
        }
    }
}
package com.soi.moya.ui.music_list

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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

    private val _musicManager = MusicManager.getInstance()
    private val _teamMusics = mutableStateOf(emptyList<Music>())
    private val _playerMusics = mutableStateOf(emptyList<Music>())
    private val _musics = _musicManager.musics["Doosan"] ?: MutableLiveData(emptyList())

    private val _selectedTeam = MutableStateFlow("doosan")
    val selectedTeam: StateFlow<String> = _selectedTeam

    val userPreferences = UserPreferences(application)

    init {
        filteringMusics()
    }

    private fun filteringMusics() {
        _musics.observeForever { musics ->
            _teamMusics.value = musics.filter { it.type }
            _playerMusics.value = musics.filter { !it.type }
        }
        viewModelScope.launch {
            userPreferences.getSelectedTeam.collect { team ->
                _selectedTeam.value = team ?: "doosan"
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

    fun toggleLike(index: Int) {
        // TODO
    }
}
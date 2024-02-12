package com.soi.moya.ui.music_list

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.Music
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicListViewModel(
    // TODO: Repository 연결
    application: Application
) : AndroidViewModel(application) {

    // TODO: 현재는 musics지만, 추후 좋아요 등의 정보를 가지고 있는 데이터로 변경이 필요
    private val _teamMusics = mutableStateOf(emptyList<Music>())
    private val _playerMusics = mutableStateOf(emptyList<Music>())

    private val _selectedTeam = MutableStateFlow("doosan")
    val selectedTeam: StateFlow<String> = _selectedTeam

    val userPreferences = UserPreferences(application)

    init {
        // TODO: 데이터 연결
        _teamMusics.value = List(10) {
            Music(id = "$it", title = "team music test $it", info = "subTitle $it")
        }
        _playerMusics.value = List(10) {
            Music(id = "$it", title = "player music test $it", info = "subTitle $it")
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
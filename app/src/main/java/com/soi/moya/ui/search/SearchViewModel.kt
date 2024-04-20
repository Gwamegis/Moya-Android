package com.soi.moya.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.MusicManager
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _musicManager = MusicManager.getInstance()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _musicFlow = MutableStateFlow<List<MusicInfo>>(emptyList())

    private val _searchResult = MutableStateFlow<List<MusicInfo>>(emptyList())
    val searchResult: StateFlow<List<MusicInfo>> = _searchResult

    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: Map<String, LiveData<List<String>>> get() = _seasonSongManager.getSeasonSongs()

    init {
        observeMusicList()
        observeSearchText()
    }

    private fun observeMusicList() {
        _musicManager.observeMusics {
            _musicManager.getAllMusicInfo().observeForever { musics ->
                _musicFlow.value = musics
            }
        }
    }

    private fun observeSearchText() {
        viewModelScope.launch {
            searchText
                .combine(_musicFlow) { searchText, musics ->
                    when {
                        searchText.isNotEmpty() -> musics.filter { music ->
                            music.title.contains(searchText, ignoreCase = true)
                        }
                        else -> emptyList()
                    }
                }.collect { result ->
                    _searchResult.value = result
                }
        }
    }

    fun setSearchText(newText: String) {
        _searchText.value = newText
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
}
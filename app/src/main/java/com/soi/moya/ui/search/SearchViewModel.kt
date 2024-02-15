package com.soi.moya.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.MusicManager
import com.soi.moya.models.MusicInfo
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


    init {
        observeMusicList()
        observeSearchText()
    }

    private fun observeMusicList() {
        viewModelScope.launch {
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
}
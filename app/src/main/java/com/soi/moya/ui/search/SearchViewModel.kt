package com.soi.moya.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    val musicList = listOf<Music>(
        Music(
            id = "1",
            info = "info",
            type = false,
            title = "title-1",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "2",
            info = "info",
            type = false,
            title = "title-2",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "3",
            info = "info",
            type = false,
            title = "title-3",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "4",
            info = "info",
            type = false,
            title = "title-4",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "5",
            info = "info",
            type = false,
            title = "title-5",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "6",
            info = "info",
            type = false,
            title = "title-6",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "1",
            info = "info",
            type = false,
            title = "title-1",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "2",
            info = "info",
            type = false,
            title = "title-2",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "3",
            info = "info",
            type = false,
            title = "title-3",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "4",
            info = "info",
            type = false,
            title = "title-4",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "5",
            info = "info",
            type = false,
            title = "title-5",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "6",
            info = "info",
            type = false,
            title = "title-6",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "1",
            info = "info",
            type = false,
            title = "title-1",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "2",
            info = "info",
            type = false,
            title = "title-2",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "3",
            info = "info",
            type = false,
            title = "title-3",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "4",
            info = "info",
            type = false,
            title = "title-4",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "5",
            info = "info",
            type = false,
            title = "title-5",
            lyrics = "lyrics",
            url = "url"
        ),
        Music(
            id = "6",
            info = "info",
            type = false,
            title = "title-6",
            lyrics = "lyrics",
            url = "url"
        )
    )

    private val _searchText = MutableStateFlow("asdf")
    val searchText: StateFlow<String> = _searchText

    private val _musicFlow = MutableStateFlow<List<Music>>(musicList)

    private val _searchResult = MutableStateFlow<List<Music>>(emptyList())
    val searchResult: StateFlow<List<Music>> = _searchResult


    init {
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
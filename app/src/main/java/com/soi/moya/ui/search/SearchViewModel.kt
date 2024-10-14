package com.soi.moya.ui.search

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.MusicManager
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val userPreferences: UserPreferences
) : ViewModel() {

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

    fun onTapListItem(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.id, "default")

                if (existingMusic != null) {
                    // 이미 저장된 음악 정보가 있을 때
                    storedMusicRepository.updateOrder(
                        start = 0,
                        end = existingMusic.order,
                        increment = 1
                    )
                    storedMusicRepository.updateOrder(existingMusic.songId, 0)
                } else {
                    // 새로운 음악 정보를 추가할 때
                    val order = storedMusicRepository.getItemCount(playlist = "default")
                    storedMusicRepository.updateOrder(
                        start = 0,
                        end = order,
                        increment = 1
                    )

                    val newMusic = music.toStoredMusic(
                        team = music.team,
                        order = 0,
                        date = Utility.getCurrentTimeString(),
                        playlist = "default"
                    )
                    storedMusicRepository.insertItem(newMusic.toItem())
                }

                saveCurrentSongId(music.id)
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

    private fun saveCurrentSongId(songId: String) {
        viewModelScope.launch {
            userPreferences.saveCurrentSongId(songId)
            userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    @SuppressLint("NewApi")
    private fun playMusic(music: MusicInfo) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }
}
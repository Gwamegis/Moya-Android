package com.soi.moya.ui.search

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.MusicManager
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.toItem
import com.soi.moya.models.toMediaItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.AddItemUseCase
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
import com.soi.moya.ui.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val application: Application,
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val mediaControllerManager: MediaControllerManager,
    private val musicStateRepository: MusicStateRepository,
    private val addItemUseCase: AddItemUseCase
) : ViewModel() {

    private val _musicManager = MusicManager.getInstance()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _musicFlow = MutableStateFlow<List<MusicInfo>>(emptyList())

    private val _searchResult = MutableStateFlow<List<MusicInfo>>(emptyList())
    val searchResult: StateFlow<List<MusicInfo>> = _searchResult

    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: Map<String, LiveData<List<String>>> get() = _seasonSongManager.getSeasonSongs()

    val currentSongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()

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
        val mediaItem = music.toMediaItem(application.filesDir.absolutePath)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.id, "default")
                val count = storedMusicRepository.getItemCount("default")
                val position: Int = if (existingMusic != null) {
                    addItemUseCase.handleExistingMusic(existingMusic, mediaItem, count)
                } else {
                    addItemUseCase.handleNewMusic(music, mediaItem, count)
                }

                withContext(Dispatchers.Main) {
                    mediaControllerManager.updateMediaController()
                }

                saveCurrentSongId(music.id, position)
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

    @SuppressLint("NewApi")
    private fun playMusic(music: MusicInfo) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }
}
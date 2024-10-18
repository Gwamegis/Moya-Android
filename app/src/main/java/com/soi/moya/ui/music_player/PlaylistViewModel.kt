package com.soi.moya.ui.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.repository.HandlePlaylistItemUseCase
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val musicStateRepository: MusicStateRepository,
    private val handlePlaylistItemUseCase: HandlePlaylistItemUseCase
) : ViewModel() {
    val currentSongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()
    val currentSongPosition: LiveData<Int?> = musicStateRepository.currentPlaySongPosition.asLiveData()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    val mediaItemList: StateFlow<List<MediaItem>> = mediaControllerManager.mediaItemList

    init {
        collectMusicIsPlaying()
    }

    private fun collectMusicIsPlaying() {
        viewModelScope.launch {
            mediaControllerManager.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }
    }

    fun deletePlaylistItem(songId: String, order: Int) {
        viewModelScope.launch {
            handlePlaylistItemUseCase.removePlaylistItem(songId, order, mediaItemList.value.count())
        }
    }

    fun onTapListItem(music: MediaItem) {
        //TODO: currentSongId가 아닌 current position을 저장
        musicStateRepository.setCurrentPlaySongId(music.mediaId)
        playMusic(music = music)
    }

    private fun playMusic(music: MediaItem) {
        viewModelScope.launch {
            musicPlaybackManager.playMediaItemById(music.mediaId)
        }
    }

    fun getMediaItemIndex(mediaId: String): Int {
        return mediaItemList.value.indexOfFirst { it.mediaId == mediaId }
    }

    fun getMediaItemIndexById(mediaId: String): Int? {
        return mediaItemList.value.indexOfFirst { it.mediaId == mediaId }.takeIf { it >= 0 }
    }
}
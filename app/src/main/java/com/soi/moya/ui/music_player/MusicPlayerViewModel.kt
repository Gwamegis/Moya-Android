package com.soi.moya.ui.music_player

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MusicPlayerManager
import com.soi.moya.ui.Utility
import com.soi.moya.ui.music_storage.MusicStorageViewModel
import com.soi.moya.ui.music_storage.StorageUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MusicPlayerViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val storedMusicRepository: StoredMusicRepository
): AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)

    private val _currentSongId = MutableLiveData<String?>()
    var music: StoredMusic? = null

    private val _songId: String
        get() = _currentSongId.value ?: ""

    private val _teamName: String = savedStateHandle["team"] ?: "doosan"
    val team: Team = Team.valueOf(_teamName)

    private val _musicPlayerManager = mutableStateOf(
        MusicPlayerManager.getInstance(
            application = application,
            storedMusicRepository = storedMusicRepository
            )
    )

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> = _isLike

    private val musicPlayerManager: State<MusicPlayerManager>
        get() = _musicPlayerManager

    val currentPosition: State<Int> get() = _currentPosition
    private val _currentPosition = mutableIntStateOf(0)

    init {
        checkItemExistence()
        startUpdateCurrentPositionAndDuration()
        subscribeCurrentSongID()
    }

    private fun startUpdateCurrentPositionAndDuration() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = musicPlayerManager.value.getCurrentPosition()
                _isPlaying.value = musicPlayerManager.value.isPlaying()
                delay(1000)
            }
        }
    }

    private fun subscribeCurrentSongID() {
        viewModelScope.launch {
            _userPreferences.currentPlaySongId.collect { songId ->
                _currentSongId.postValue(songId)
                if (songId != null) {
                    val liked = storedMusicRepository.isSongLiked(songId)
                    _isLike.value = liked
                }
            }
        }
    }

    fun togglePlayPause() {
        musicPlayerManager.value.togglePlayPause()
    }

    fun playNextSong(increment: Int) {
        musicPlayerManager.value.playNextSong(increment)
    }

    fun formatTime(time: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getDuration(): Int {
        return musicPlayerManager.value.getDuration()
    }

    fun seekTo(position: Int) {
        musicPlayerManager.value.seekTo(position)
    }

    fun popBackStack(navController: NavHostController) {
        stopMusic()
        navController.popBackStack()
    }

    fun stopMusic() {
        _musicPlayerManager.value.stop()
    }

    //좋아요 관련 함수
    fun updateLikeMusic(music: MusicInfo) {
        val newLikeStatus = !isLike.value
        _isLike.value = newLikeStatus

        if (newLikeStatus) {
            likeMusic(music)
        } else {
            unlikeMusic(music)
        }
    }

    private fun likeMusic(music: MusicInfo) {
        viewModelScope.launch {
            val order = storedMusicRepository.getItemCount(playlist = "favorite")
            val music = music.toStoredMusic(
                team = team,
                order = order,
                date = Utility.getCurrentTimeString(),
                playlist = "favorite"
            )

            withContext(Dispatchers.IO) {
                storedMusicRepository.insertItem(music.toItem())
            }
        }
    }

    private fun unlikeMusic(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.deleteById(id = music.id, playlist = "favorite")
            }
        }
    }
    private suspend fun doesItemExist(itemId: String): Boolean {
        return storedMusicRepository.doesItemExist(itemId = itemId, playlist = "favorite")
    }
    private fun checkItemExistence() {
        viewModelScope.launch {
            _isLike.value = _musicPlayerManager.value.currentMusic.value?.let { doesItemExist(it.songId) } == true
        }
    }
}
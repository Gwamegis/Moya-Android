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
import com.soi.moya.data.PlayListViewModel
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MusicPlayerManager
import com.soi.moya.ui.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MusicPlayerViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val storedMusicRepository: StoredMusicRepository,
    private var playListViewModel: PlayListViewModel
) : AndroidViewModel(application) {

    private val _items = MutableStateFlow<List<MusicInfo>>(emptyList())
    private val _currentSongId = MutableLiveData<String?>()

    private val _songId: String
        get() = _currentSongId.value ?: ""

    private val _teamName: String = savedStateHandle["team"] ?: "doosan"
    val team: Team = Team.valueOf(_teamName)

    private val _musicPlayerManager =
        mutableStateOf(MusicPlayerManager.getInstance(application = application))

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> = _isLike

    val currentPosition: State<Int> get() = _currentPosition
    private val _currentPosition = mutableIntStateOf(0)

    private val _toastMessageResourceId = MutableSharedFlow<Int>()
    val toastMessageResourceId = _toastMessageResourceId.asSharedFlow()

    private val _userPreferences = UserPreferences(application)

    init {
        checkItemExistence(_songId)
        startUpdateCurrentPositionAndDuration()
        collectIsPlayBackCompleted()
        observePlayListSongs()
    }

    private fun handlePlaybackCompletion() {
        _musicPlayerManager.value.currentPlayingMusic.value?.let { music ->
            playNextMusic(music)
        }
    }

    fun sendMessage(message: Int) {
        viewModelScope.launch {
            _toastMessageResourceId.emit(message)
        }
    }

    private fun startUpdateCurrentPositionAndDuration() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = _musicPlayerManager.value.getCurrentPosition()
                _isPlaying.value = _musicPlayerManager.value.isPlaying()
                delay(1000)
            }
        }
    }

    fun togglePlayPause() {
        _musicPlayerManager.value.togglePlayPause()
    }

    private fun saveCurrentSongId(songId: String) {
        viewModelScope.launch {
            _userPreferences.saveCurrentSongId(songId)
            _userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    fun playPrevMusic(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val previousMusic = playListViewModel.getPreviousMusic(music)
                previousMusic?.let { music ->
                    _musicPlayerManager.value.playMusic(music)
                    saveCurrentSongId(songId = music.id)
                }
            }
        }
    }

    fun playNextMusic(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val nextMusic = playListViewModel.getNextMusic(music)
                nextMusic?.let { music ->
                    _musicPlayerManager.value.playMusic(music)
                    saveCurrentSongId(songId = music.id)
                } ?: run {
                    playFirstMusic()
                }
            }

        }
    }

    private fun playFirstMusic() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playListViewModel.getFirstMusic()?.let { firstMusic ->
                    _musicPlayerManager.value.playMusic(firstMusic)
                    saveCurrentSongId(songId = firstMusic.id)
                }
            }
        }
    }

    fun formatTime(time: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time.toLong())
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(time.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getDuration(): Int {
        return _musicPlayerManager.value.getDuration()
    }

    fun seekTo(position: Int) {
        _musicPlayerManager.value.seekTo(position)
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
            val music = music!!.toStoredMusic(
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
                storedMusicRepository.deleteById(id = music!!.id, playlist = "favorite")
            }
        }
    }

    suspend fun doesItemExist(itemId: String): Boolean {
        return storedMusicRepository.doesItemExist(itemId = itemId)
    }

    private fun checkItemExistence(itemId: String) {
        viewModelScope.launch {
            _isLike.value = doesItemExist(_songId) ?: false
        }
    }

    private fun collectIsPlayBackCompleted() {
        viewModelScope.launch {
            _musicPlayerManager.value.isPlaybackCompleted.collect { isPlaybackCompleted ->
                if (isPlaybackCompleted) {
                    handlePlaybackCompletion()
                }
            }
        }
    }

    private fun observePlayListSongs() {
        viewModelScope.launch {
            playListViewModel.allItems.observeForever { listOfItems ->
                _items.value = listOfItems
            }
        }
    }
}
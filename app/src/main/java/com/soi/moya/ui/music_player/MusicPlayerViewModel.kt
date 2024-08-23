package com.soi.moya.ui.music_player

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.playback.MoyaPlaybackService
import com.soi.moya.repository.MusicPlayerManager
import com.soi.moya.ui.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

data class PlayerState(
    val player: Player? = null,
    val currentMediaTitle: String = "",
    val currentMediaArtist: String = "",
    val mediaItems: List<MediaItem> = emptyList(),
    val currentMediaItemIndex: Int = -1
)

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
    private val musicPlayerManager: State<MusicPlayerManager>
        get() = _musicPlayerManager

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> = _isLike

    val currentPosition: State<Long> get() = _currentPosition
    private val _currentPosition = mutableLongStateOf(0)

    private val _isLyricDisplaying = MutableStateFlow(true)
    val isLyricDisplaying: StateFlow<Boolean> = _isLyricDisplaying

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

//    private var mediaController: MediaController? = null

    init {
//        viewModelScope.launch {
//            initializeController(context = application)
//        }
        subscribeIsLyricView()
        startUpdateCurrentPositionAndDuration()
        subscribeCurrentSongID()
    }

//    private suspend fun initializeController(context: Context) {
//        mediaController = withContext(Dispatchers.IO) {
//            MediaController.Builder(
//                context,
//                SessionToken(context, ComponentName(context, MoyaPlaybackService::class.java))
//            ).buildAsync().get()
//        }
//
//        mediaController?.let { controller ->
//            _state.value = state.value.copy(player = controller)
//
//            controller.addListener(object : Player.Listener {
//                override fun onEvents(player: Player, events: Player.Events) {
//                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
//                        updateUI()
//                    }
//                }
//            })
//        }
//    }

    private fun updateUI() {
//        mediaController?.let { controller ->
//            _state.value = PlayerState(
//                player = controller,
//                currentMediaTitle = controller.mediaMetadata.title.toString() ?: "",
//                currentMediaArtist = controller.mediaMetadata.artist.toString() ?: "",
//                mediaItems = List(controller.mediaItemCount) { index -> controller.getMediaItemAt(index) },
//                currentMediaItemIndex = controller.currentMediaItemIndex
//            )
//        }
    }

//    fun onMediaItemClick(index: Int) {
//        mediaController?.let { controller ->
//            if (controller.currentMediaItemIndex == index) {
//                controller.playWhenReady = !controller.playWhenReady
//            } else {
//                controller.seekToDefaultPosition(index)
//            }
//            updateUI()
//        }
//    }
//
//    fun onDeleteMediaItem(index: Int) {
//        mediaController?.let { controller ->
//            controller.removeMediaItem(index)
//            updateUI()
//        }
//    }
//
//    override fun onCleared() {
//        mediaController?.release()
//        super.onCleared()
//    }

//    init {
//        startUpdateCurrentPositionAndDuration()
//        subscribeCurrentSongID()
//        subscribeIsLyricView()
//    }

    private fun startUpdateCurrentPositionAndDuration() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = musicPlayerManager.value.getCurrentPosition()
                _isPlaying.value = musicPlayerManager.value.isPlaying.value
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

    private fun subscribeIsLyricView() {
        viewModelScope.launch {
            _userPreferences.isLyricDisplaying.collect { value ->
                _isLyricDisplaying.value = value
            }
        }
    }

    fun toggleisLyricDisplaying() {
        viewModelScope.launch {
            _userPreferences.saveIsLyricState(!_isLyricDisplaying.value)
        }
    }

    fun togglePlayPause() {
        musicPlayerManager.value.togglePlayPause()
    }

    fun playNextSong(increment: Int) {
        musicPlayerManager.value.playNextSong(increment)
    }

    fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getDuration(): Long {
        return musicPlayerManager.value.getDuration()
    }

    fun seekTo(position: Long) {
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
            val item = music.toStoredMusic(
                team = music.team,
                order = order,
                date = Utility.getCurrentTimeString(),
                playlist = "favorite"
            )

            withContext(Dispatchers.IO) {
                storedMusicRepository.insertItem(item.toItem())
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
}
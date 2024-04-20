package com.soi.moya.ui.music_player

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.soi.moya.data.MusicManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MusicPlayerManager
import com.soi.moya.ui.Utility
import com.soi.moya.ui.moyaApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class MusicPlayerViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val storedMusicRepository: StoredMusicRepository
): AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)
    private val _currentSongId = MutableLiveData<String?>()
    var music: MusicInfo? = null

    init {
        viewModelScope.launch {
            _userPreferences.currentPlaySongId.collect { songId ->
                _currentSongId.value = songId ?: ""
                // music 초기화
                music = if (_currentSongId.value?.isNotEmpty() == true) {
                    MusicManager.getInstance().getMusicById(_currentSongId.value!!)
                } else {
                    null
                }
            }
        }
    }

    private val _songId: String
        get() = _currentSongId.value ?: ""

    private val _teamName: String = savedStateHandle["team"] ?: "doosan"
    val team: Team = Team.valueOf(_teamName)

    private val _musicPlayerManager = mutableStateOf(MusicPlayerManager.getInstance())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> = _isLike

    private val musicPlayerManager: State<MusicPlayerManager>
        get() = _musicPlayerManager

    val currentPosition: State<Int> get() = _currentPosition
    private val _currentPosition = mutableIntStateOf(0)

    init {
        playMusic(filePath = application.filesDir.absolutePath)
        checkItemExistence(_songId)
    }

    companion object {
        val Factory: ViewModelProvider.Factory= object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelCalss: Class<T>,
                extras: CreationExtras
            ) : T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()
                val storedMusicRepository = extras.moyaApplication().container.itemsRepository

                return MusicPlayerViewModel(
                    application,
                    savedStateHandle,
                    storedMusicRepository
                ) as T
            }
        }
    }

    private fun playMusic(filePath: String) {
        viewModelScope.launch {
            val music = music ?: return@launch
            val file = File(filePath, "${music.id}-${music.title}.mp3")
            if(!file.exists()) {
                downloadFileAsync(music.url, file.absolutePath)
            }
            _musicPlayerManager.value.playMusicFromUrl(file.absolutePath)
            startUpdateCurrentPositionAndDuration()
        }
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

    fun togglePlayPause() {
        musicPlayerManager.value.togglePlayPause()
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

    //음악 다운로드
    private suspend fun downloadFileAsync(url: String, filePath: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                if(connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream: InputStream = connection.inputStream
                    val file = File(filePath)
                    val fileOutputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while(inputStream.read(buffer).also { bytesRead = it } != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead)
                    }

                    fileOutputStream.close()
                    inputStream.close()

                    return@withContext file
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }

    //좋아요 관련 함수
    fun updateLikeMusic() {
        val newLikeStatus = !isLike.value
        _isLike.value = newLikeStatus

        if (newLikeStatus) {
            likeMusic()
        } else {
            unlikeMusic()
        }
    }

    private fun likeMusic() {
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

    private fun unlikeMusic() {
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
}
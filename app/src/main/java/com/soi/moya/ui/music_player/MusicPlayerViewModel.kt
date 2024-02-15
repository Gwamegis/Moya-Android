package com.soi.moya.ui.music_player

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.soi.moya.data.MusicManager
import com.soi.moya.models.Music
import com.soi.moya.repository.MusicPlayerManager
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
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    private val songId: String =  checkNotNull(savedStateHandle["songId"])
    val music: Music = MusicManager.getInstance().getMusicById(songId)

    private val _musicPlayerManager = mutableStateOf(MusicPlayerManager(application))

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val musicPlayerManager: State<MusicPlayerManager>
        get() = _musicPlayerManager

    val currentPosition: State<Int> get() = _currentPosition
    private val _currentPosition = mutableIntStateOf(0)

    init {
        playMusic(filePath = application.filesDir.absolutePath)
        music?.lyrics?.let { Log.d("_________", it) }
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

                return MusicPlayerViewModel(
                    application,
                    savedStateHandle
                ) as T
            }
        }
    }

    private fun playMusic(filePath: String) {
        viewModelScope.launch {
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

    fun updateLikeMusic(isLike: Boolean) {
        if (isLike) {
            unlikeMusic()
        } else {
            likeMusic()
        }
    }

    fun formatTime(time: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun likeMusic() {

    }

    private fun unlikeMusic() {

    }

    fun getDuration(): Int {
        return musicPlayerManager.value.getDuration()
    }

    fun seekTo(position: Int) {
        musicPlayerManager.value.seekTo(position)
    }

    fun popBackStack(navController: NavHostController) {
        _musicPlayerManager.value.stop()
        navController.popBackStack()
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
}
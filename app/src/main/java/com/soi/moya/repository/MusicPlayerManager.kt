package com.soi.moya.repository

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.soi.moya.data.MusicManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toMusicInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MusicPlayerManager private constructor(
    private val application: Application,
    private val storedMusicRepository: StoredMusicRepository
) {
    companion object {
        @Volatile
        private var instance: MusicPlayerManager? = null

        fun getInstance(
            application: Application,
            storedMusicRepository: StoredMusicRepository
        ) =
            instance ?: synchronized(this) {
                instance ?: MusicPlayerManager(
                    application,
                    storedMusicRepository
                ).also { instance = it }
            }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
    }

    private val musicManager = MusicManager.getInstance()

    private var currentStoredMusicList = listOf<StoredMusic>()

    private var _currentMusic = mutableStateOf<StoredMusic?>(null)
    val currentMusic: State<StoredMusic?> get() = _currentMusic

    private val _userPreferences = UserPreferences(application)

    init {
        observeCurrentSongId()
        observeStoredMusicChanges()
    }

    private fun observeCurrentSongId() {
        coroutineScope.launch {
            _userPreferences.currentPlaySongId.collectLatest { songId ->
                if (songId != null) {
                    val musicInfo = currentStoredMusicList.find { it.songId == songId }
                    _currentMusic.value = musicInfo
                }
            }
        }
    }

    private fun observeStoredMusicChanges() {
        coroutineScope.launch {
            storedMusicRepository.getByDefaultPlaylist().collect { storedMusicList ->
                currentStoredMusicList = storedMusicList
            }
        }
    }
    fun playNextSong(increment: Int) {
        var nextOrder = _currentMusic.value?.order?.plus(increment)

        if (nextOrder == currentStoredMusicList.count()) {
            nextOrder = 0
        } else if (nextOrder != null) {
            if (nextOrder < 0) {
                nextOrder = currentStoredMusicList.count() - 1
            }
        }
        val nextSong = currentStoredMusicList.find { it.order == nextOrder }

        if (nextSong != null) {
            saveCurrentSongId(nextSong.songId)
            playMusic(nextSong.toMusicInfo())
        }
    }


    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            play()
        } else {
            pause()
        }
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun stop() {
        if (mediaPlayer.isPlaying) {
            // prepare를 다시 호출하지 않기 위해 pause 및 seekto 사용
            pause()
            seekTo(0)
        }
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    private fun playMusicFromUrl(url: String) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(url)
                prepare()
                play()
            }.setOnCompletionListener {
                playNextSong(1)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    fun playMusic(currentMusic: MusicInfo) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val music = currentMusic
            val file = File(filePath, "${music.id}-${music.title}.mp3")
            if(!file.exists()) {
                downloadFileAsync(music.url, file.absolutePath)
            }
            playMusicFromUrl(file.absolutePath)
        }
    }

    fun playMusic(currentMusic: StoredMusic) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val music = currentMusic
            val file = File(filePath, "${music.songId}-${music.title}.mp3")
            if(!file.exists()) {
                downloadFileAsync(music.url, file.absolutePath)
            }
            playMusicFromUrl(file.absolutePath)
        }
    }

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

    private fun saveCurrentSongId(songId: String) {
        coroutineScope.launch {
            _userPreferences.saveCurrentSongId(songId)
        }
    }
}
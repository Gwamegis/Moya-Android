package com.soi.moya.repository

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.soi.moya.models.MusicInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MusicPlayerManager private constructor(private val application: Application) {
    companion object {
        @Volatile
        private var instance: MusicPlayerManager? = null

        fun getInstance(application: Application) =
            instance ?: synchronized(this) {
                instance ?: MusicPlayerManager(application).also { instance = it }
            }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isPlaybackCompleted = MutableStateFlow(false)
    val isPlaybackCompleted: StateFlow<Boolean> = _isPlaybackCompleted

    private val _currentPlayingMusic = MutableStateFlow<MusicInfo?>(null)
    val currentPlayingMusic: StateFlow<MusicInfo?> = _currentPlayingMusic

    private val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )

        setOnCompletionListener {
            _isPlaybackCompleted.value = true
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
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun playMusic(currentMusic: MusicInfo) {
        _currentPlayingMusic.value = currentMusic
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
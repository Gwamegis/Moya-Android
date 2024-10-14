package com.soi.moya.repository

import android.app.Application
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.util.UnstableApi
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class MusicPlaybackManager @Inject constructor(
    private val controllerManager: MediaControllerManager,
    private val application: Application,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    fun play() {
        controllerManager.controller?.play()
    }

    fun pause() {
        controllerManager.controller?.pause()
    }

    fun togglePlayPause() {
        controllerManager.controller?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun seekTo(position: Long) {
        controllerManager.controller?.seekTo(position)
    }

    fun playNextSong(increment: Int) {
        if (increment == 1) {
            controllerManager.controller?.seekToNextMediaItem()
        } else if (increment == -1) {
            controllerManager.controller?.seekToPreviousMediaItem()
        }
    }

    fun stop() {
        controllerManager.controller?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }

    fun playMediaItemById(mediaItemId: String) {
        val mediaItems = controllerManager.mediaItemList.value
        val index = mediaItems.indexOfFirst { it.mediaId == mediaItemId }
        if (index != -1) {
            controllerManager.controller?.let {
                it.seekToDefaultPosition(index)
                it.prepare()
                it.play()
            }
        }
    }

    fun getCurrentPosition(): Long {
        return controllerManager.controller?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return controllerManager.controller?.duration ?: 0L
    }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun playMusic(currentMusic: MusicInfo) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val file = File(filePath, "${currentMusic.id}-${currentMusic.title}.mp3")
            if (!file.exists()) {
                downloadFileAsync(currentMusic.url, file.absolutePath)
            }

            controllerManager.controller?.prepare()
            controllerManager.controller?.play()
        }
    }

    fun playMusic(currentMusic: StoredMusic) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val file = File(filePath, "${currentMusic.songId}-${currentMusic.title}.mp3")
            if (!file.exists()) {
                downloadFileAsync(currentMusic.url, file.absolutePath)
            }

            controllerManager.controller?.prepare()
            controllerManager.controller?.play()
        }
    }

    private suspend fun downloadFileAsync(url: String, filePath: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream: InputStream = connection.inputStream
                    val file = File(filePath)
                    val fileOutputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead)
                    }

                    fileOutputStream.close()
                    inputStream.close()
                    return@withContext file
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                return@withContext  null
            }
        }
    }
}

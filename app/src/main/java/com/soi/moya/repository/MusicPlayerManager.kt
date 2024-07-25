package com.soi.moya.repository

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.soi.moya.data.MusicManager
import com.soi.moya.data.StoredMusicDao
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

    private val exoPlayer = ExoPlayer.Builder(application)
        .setAudioAttributes(androidx.media3.common.AudioAttributes.DEFAULT, true)
        .build()

//    private val mediaPlayer = MediaPlayer().apply {
//        setAudioAttributes(
//            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
//        )
//    }

    private fun saveCurrentSongId(mediaId: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val playlistItem = storedMusicRepository.getItemById(mediaId, "default")
                playlistItem?.let {
                    _userPreferences.saveCurrentSongId(it.id)
                }
            }
        }
    }

    private val musicManager = MusicManager.getInstance()

//    private var currentStoredMusicList = listOf<StoredMusic>()

//    private var _currentMusic = mutableStateOf<StoredMusic?>(null)
//    val currentMusic: State<StoredMusic?> get() = _currentMusic

    private val _userPreferences = UserPreferences(application)

    init {
//        observeCurrentSongId()
//        observeStoredMusicChanges()
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        loadPlaylist()
        observeCurrentSongId()
    }

    private fun observeCurrentSongId() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                coroutineScope.launch {
                    exoPlayer.currentMediaItem?.let { _userPreferences.saveCurrentSongId(it.mediaId) }
                }
            }
        })
    }

//    private fun observeCurrentSongId() {
//        coroutineScope.launch {
//            _userPreferences.currentPlaySongId.collectLatest { songId ->
//                if (songId != null) {
//                    val musicInfo = currentStoredMusicList.find { it.songId == songId }
//                    _currentMusic.value = musicInfo
//                }
//            }
//        }
//    }

//    private fun observeStoredMusicChanges() {
//        coroutineScope.launch {
//            storedMusicRepository.getByDefaultPlaylist().collect { storedMusicList ->
//                currentStoredMusicList = storedMusicList
//            }
//        }
//    }


//    fun playNextSong(increment: Int) {
//        var nextOrder = _currentMusic.value?.order?.plus(increment)
//
//        if (nextOrder == currentStoredMusicList.count()) {
//            nextOrder = 0
//        } else if (nextOrder != null) {
//            if (nextOrder < 0) {
//                nextOrder = currentStoredMusicList.count() - 1
//            }
//        }
//        val nextSong = currentStoredMusicList.find { it.order == nextOrder }
//
//        if (nextSong != null) {
//            saveCurrentSongId(nextSong.songId)
//            playMusic(nextSong.toMusicInfo())
//        }
//    }

    @OptIn(UnstableApi::class)
    fun playNextSong(increment: Int) {
        if (increment == 1) {
            exoPlayer.seekToNextMediaItem()
        } else if (increment == -1) {
            exoPlayer.seekToPreviousMediaItem()
        }

    }


    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }

    fun getDuration(): Long {
        return exoPlayer.duration
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    fun stop() {
        if (exoPlayer.isPlaying) {
            // prepare를 다시 호출하지 않기 위해 pause 및 seekto 사용
            exoPlayer.pause()
            exoPlayer.seekTo(0)
        }
    }

    fun play() {
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    private fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    private fun playMusicFromUrl(url: String, id: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setMediaId(id)  // mediaId 설정
            .build()
        exoPlayer.apply {
            addMediaItem(0, mediaItem)
            prepare()
            seekTo(0, 0)
            play()  // 재생
        }
    }

    fun releasePlayer() {
        exoPlayer.release()
    }

    fun playMusic(currentMusic: MusicInfo) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val music = currentMusic
            val file = File(filePath, "${music.id}-${music.title}.mp3")
            if(!file.exists()) {
                downloadFileAsync(music.url, file.absolutePath)
            }
            playMusicFromUrl(file.absolutePath, currentMusic.id)
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
            playMusicFromUrl(file.absolutePath, currentMusic.songId)
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

    fun getMusicItemList() {

    }

    private fun loadPlaylist() {
        coroutineScope.launch {
            storedMusicRepository.getByDefaultPlaylist().collect { playlistItems ->
                exoPlayer.clearMediaItems() // 기존 재생목록 초기화
                playlistItems.forEach { item ->
                    val mediaItem = MediaItem.fromUri(item.url)
                    exoPlayer.addMediaItem(mediaItem)
                }
            }
        }
    }

}
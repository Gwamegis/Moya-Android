package com.soi.moya.repository

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.net.MediaType
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.soi.moya.R
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.playback.MoyaPlaybackService
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

    private val mediaItemList: MutableList<MediaItem> = mutableListOf()

    private lateinit var controllerFuture: ListenableFuture<MediaController>

    private val controller: MediaController?
        get() =
            if (controllerFuture.isDone && !controllerFuture.isCancelled) controllerFuture.get() else null

    private val _userPreferences = UserPreferences(application)

    init {
        initializeController()
    }
    private fun initializeController() {
        controllerFuture =
            MediaController.Builder(
                application,
                SessionToken(application, ComponentName(application, MoyaPlaybackService::class.java)),
            ).buildAsync()
        updateMediaMetadataUI()
        controllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }

    @OptIn(UnstableApi::class) // PlayerView.setShowSubtitleButton
    private fun setController() {
        val controller = this.controller ?: return
        updateCurrentPlaylistUI()

        controller.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                    }
                    if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                        updateCurrentPlaylistUI()
                    }
                    if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                        updateMediaMetadataUI()
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        // Trigger adapter update to change highlight of current item.
                        // 리스트 변경되어야한다고 알리기
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            }
        )
    }

    private fun updateMediaMetadataUI() {
        val controller = this.controller
        if (controller == null || controller.mediaItemCount == 0) {
            //기다리는 중 표시
            return
        }

        val mediaMetadata = controller.mediaMetadata
        val title: CharSequence = mediaMetadata.title ?: ""

        //title 설정
        //artist 설정
    }

    //변경된 플레이리스트로 업데이트
    private fun updateCurrentPlaylistUI() {
        val controller = this.controller ?: return
        mediaItemList.clear()
        for (i in 0 until controller.mediaItemCount) {
            mediaItemList.add(controller.getMediaItemAt(i))
        }
        //플레이리스트 변경되었다고 알림보내기
    }


    fun playNextSong(increment: Int) {
        if (increment == 1) {
            controller?.seekToNextMediaItem()
        } else if (increment == -1) {
            controller?.seekToPreviousMediaItem()
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }
    fun stop() {
        controller?.let {
            if (it.isPlaying) {
                it?.pause()
                it?.seekTo(0)
            }
        }
    }

    fun play() {
        controller?.play()
    }

    fun releaseController() {
        controller?.release()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playMusic(currentMusic: MusicInfo) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val file = File(filePath, "${currentMusic.id}-${currentMusic.title}.mp3")
            if (!file.exists()) {
                downloadFileAsync(currentMusic.url, file.absolutePath)
            }

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.fromFile(file))
                .setMediaId(currentMusic.id)
                .build()

            controller?.addMediaItem(mediaItem)
            controller?.prepare()
            controller?.play()
        }
    }

    fun playMusic(currentMusic: StoredMusic) {
        val filePath = application.filesDir.absolutePath
        coroutineScope.launch {
            val file = File(filePath, "${currentMusic.songId}-${currentMusic.title}.mp3")
            if (!file.exists()) {
                downloadFileAsync(currentMusic.url, file.absolutePath)
            }

            val resourceId = if (currentMusic.type) Team.valueOf(currentMusic.team).getPlayerAlbumImageResourceId() else Team.valueOf(currentMusic.team).getTeamImageResourceId()
            val imageUri = Uri.parse("android.resource://com.soi.moya/$resourceId")

            val mediaItem = buildMediaItem(
                title = currentMusic.title,
                mediaId = currentMusic.songId,
                mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
                artist = currentMusic.team,
                sourceUri = Uri.fromFile(file),
                imageUri = imageUri
            )

            controller?.addMediaItem(mediaItem)
            controller?.prepare()
            controller?.play()
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

    fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean = true,
        isBrowsable: Boolean = false,
        mediaType: @MediaMetadata.MediaType Int,
        subtitleConfigurations: List<MediaItem.SubtitleConfiguration> = mutableListOf(),
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setIsBrowsable(isBrowsable)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .setMediaType(mediaType)
                .build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setSubtitleConfigurations(subtitleConfigurations)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    private fun loadPlaylist() {
        coroutineScope.launch {
//            storedMusicRepository.getByDefaultPlaylist().collect { playlistItems ->
//                exoPlayer.clearMediaItems()
//                playlistItems.forEach { item ->
//                    val mediaItem = MediaItem.Builder()
//                        .setUri(Uri.parse(item.url))
//                        .setMediaId(item.songId)
//                        .build()
//                    exoPlayer.addMediaItem(mediaItem)
//                }
//                exoPlayer.prepare()
//            }
        }
    }
    fun getCurrentPosition(): Long {
        return controller?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return controller?.duration ?: 0L
    }
}
package com.soi.moya.playback

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.soi.moya.R
import com.soi.moya.models.Team
import com.soi.moya.ui.MoyaApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

open class MoyaPlaybackService() : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var application: MoyaApplication
    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "moya_session_notification_channel_id"
        private const val CHANNEL_NAME = "Moya Media Playback"
        const val SAVE_TO_FAVORITES = "com.moya.save_to_favorites"
    }

    open fun getSingleTopActivity(): PendingIntent? = null
    open fun getBackStackedActivity(): PendingIntent? = null
    protected open fun createSessionCallback(): MediaSession.Callback {
        return MoyaMediaSessionCallback(this)
    }
    override fun onGetSession(controllerInfo: ControllerInfo): MediaSession? = mediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        application = getApplication() as MoyaApplication

        initializeSessionAndPlayer()
        setListener(MediaSessionServiceListener())

        CoroutineScope(Dispatchers.IO).launch {
            loadInitialPlaylist(application)
        }
    }

    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build()
        player.addAnalyticsListener(EventLogger())
        player.repeatMode = Player.REPEAT_MODE_ALL

        /*
        * ForwardingPlayer :Android의 Media3 라이브러리에서 제공하는 추상 클래스
        * Player인터페이스를 구현하는 여러 플레이어 객체를 감싸서 다양한 플레이어에 대한 공통된 기능을 제공하는데 사용 */

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(createSessionCallback())
            .build()
    }

    @SuppressLint("Range")
    @OptIn(UnstableApi::class)
    suspend fun loadInitialPlaylist(application: MoyaApplication) {
        val playlist = application.container.itemsRepository.getByDefaultPlaylist()

        val filePath = application.filesDir.absolutePath
        val mediaItems = playlist.map { songs ->
            songs.map { song ->
                val file = File(filePath, "${song.songId}-${song.title}.mp3")
                val resourceId = if (song.type) Team.valueOf(song.team).getPlayerAlbumImageResourceId() else Team.valueOf(song.team).getTeamImageResourceId()
                val imageUri = Uri.parse("android.resource://com.soi.moya/$resourceId")

                buildMediaItem(
                    title = song.title,
                    mediaId = song.songId,
                    mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
                    artist = Team.valueOf(song.team).getKrTeamName(),
                    sourceUri = Uri.fromFile(file),
                    imageUri = imageUri
                )
            }
        }
        mediaItems.collect { items ->
            withContext(Dispatchers.Main) {
                player.setMediaItems(items)
            }
        }
    }

    private fun buildMediaItem(
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

    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {
        @SuppressLint("ResourceType")
        override fun onForegroundServiceStartNotAllowedException() {
            if (
                Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Notification permission is required but not granted
                return
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this@MoyaPlaybackService)
            ensureNotificationChannel(notificationManagerCompat)
            val builder =
                NotificationCompat.Builder(this@MoyaPlaybackService, CHANNEL_ID)
                    .setSmallIcon(R.drawable.moya_logo)
                    .setColor(getColor(Color.WHITE))
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_content_text))
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .also { builder -> getBackStackedActivity()?.let { builder.setContentIntent(it) } }
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }

        private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
            if (
                Build.VERSION.SDK_INT < 26 ||
                notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null
            ) {
                return
            }

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val player = mediaSession.player
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }
    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        getBackStackedActivity()?.let { mediaSession.setSessionActivity(it) }
        mediaSession.release()
        mediaSession.player.release()
        clearListener()
        super.onDestroy()
    }

    private inner class MoyaMediaSessionCallback(
        private val context: Context
    ): MediaSession.Callback {
        /*
        * MediaSession의 클라이언트인 MediaController가 MediaSession에 연결할때 호출되는 콜백 메서드로
        * 클라이언트가 세션에 연결될 때 어떤 명령어를 사용할 수 있는지를 정의함
        *
        * 명령어 집합 정의
        * 명령어 집합 반환*/

        @OptIn(UnstableApi::class)
        val mediaNotificationSessionCommands =
            ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
                .build()
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: ControllerInfo
        ): ConnectionResult {
            if (
                session.isMediaNotificationController(controller) ||
                session.isAutomotiveController(controller) ||
                session.isAutoCompanionController(controller)
            ) {
                // Select the button to display.
                return ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(mediaNotificationSessionCommands)
                    .build()
            }
            // Default commands without custom layout for common controllers.
            return ConnectionResult.AcceptedResultBuilder(session).build()
        }

        @UnstableApi
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            val settable = SettableFuture.create<MediaItemsWithStartPosition>()
            scope.launch {
                try {
                    // Call the restorePlaylist function to get the playlist
                    val resumptionPlaylist =
                        withContext(Dispatchers.IO) {
//                            restorePlaylist().get()
                        }  // Block until the future completes
//                    settable.set(resumptionPlaylist)
                } catch (e: Exception) {
                    settable.setException(e)
                }
            }
            return settable
        }
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            return Futures.immediateFuture(resolveMediaItems(mediaItems))
        }

        @OptIn(UnstableApi::class)
        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: ControllerInfo,
            mediaItems: List<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ): ListenableFuture<MediaItemsWithStartPosition> {
            return Futures.immediateFuture(
                MediaItemsWithStartPosition(resolveMediaItems(mediaItems), startIndex, startPositionMs)
            )
        }
        // 현재 재생 목록을 가져오는 함수
        fun getCurrentMediaItems(exoPlayer: ExoPlayer): List<MediaItem> {
            val mediaItems = mutableListOf<MediaItem>()
            for (i in 0 until exoPlayer.mediaItemCount) {
                exoPlayer.getMediaItemAt(i).let { mediaItems.add(it) }
            }
            return mediaItems
        }


        //??
        @OptIn(UnstableApi::class)
        private fun restorePlaylist(): ListenableFuture<MediaItemsWithStartPosition> {
            // Create a SettableFuture to return
            val settableFuture = SettableFuture.create<MediaItemsWithStartPosition>()

            scope.launch {
                // Retrieve playlist and start position from your data source
                val playlist = application.container.itemsRepository.getByDefaultPlaylist()  // List<StoredMusic>
                val startPositionMs: Long = 0  // Long (start position)

                // Map your StoredMusic to MediaItem
                val mediaItems = playlist.map { songs ->
                    songs.map { song ->
                        MediaItem.Builder()
                            .setUri(Uri.parse(song.url))  // Media file URI
                            .setMediaId(song.id)  // Media ID
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(song.title)  // Title
                                    .setArtist(song.team)  // Artist
                                    .build()
                            )
                            .build()
                    }
                }

                mediaItems.collect {items ->
                    val mediaItemsWithStartPosition = MediaItemsWithStartPosition(items, 0, startPositionMs)
                    settableFuture.set(mediaItemsWithStartPosition)
                }
            }

            return settableFuture
        }

        private fun resolveMediaItems(mediaItems: List<MediaItem>): List<MediaItem> {
            val playlist = getCurrentMediaItems(player)

            // 새 미디어 아이템을 리스트에 추가합니다.
            val currentPlaylist = playlist.toMutableList()

            mediaItems.forEach { item ->
                currentPlaylist.add(0, item)
            }

            return currentPlaylist
        }
    }
}

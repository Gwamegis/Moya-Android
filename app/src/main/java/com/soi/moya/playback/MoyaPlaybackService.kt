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
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.soi.moya.R
import com.soi.moya.data.OfflineItemsRepository
import com.soi.moya.models.Team
import com.soi.moya.repository.MusicStateRepository
import com.soi.moya.ui.MoyaApplication
import com.soi.moya.ui.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
open class MoyaPlaybackService() : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var application: MoyaApplication

    private lateinit var mediaRepository: OfflineItemsRepository

    @Inject
    lateinit var musicStateRepository: MusicStateRepository

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
            loadPlaylist(application)
        }
    }

    override fun stopService(name: Intent?): Boolean {
        Log.d("**stop service", "stop service moya play back service")
        stopSelf()
        return super.stopService(name)
    }

    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build()

        player?.addAnalyticsListener(EventLogger())
        player?.repeatMode = Player.REPEAT_MODE_ALL

        /*
        * ForwardingPlayer :Android의 Media3 라이브러리에서 제공하는 추상 클래스
        * Player인터페이스를 구현하는 여러 플레이어 객체를 감싸서 다양한 플레이어에 대한 공통된 기능을 제공하는데 사용
        */

        mediaSession = MediaSession.Builder(this, player!!)
            .setCallback(createSessionCallback())
            .build()
    }

    @SuppressLint("Range")
    @OptIn(UnstableApi::class)
    suspend fun loadPlaylist(application: MoyaApplication) {
        val playlist = withContext(Dispatchers.IO) {
            application.container.itemsRepository.getByDefaultPlaylist()
                .firstOrNull() ?: emptyList()
        }
        val filePath = application.filesDir.absolutePath
        val mediaItems = playlist.mapNotNull { song ->
            val file = File(filePath, "${song.songId}-${song.title}.mp3")
            if (!file.exists()) return@mapNotNull null

            val resourceId = if (song.type) {
                Team.valueOf(song.team).getPlayerAlbumImageResourceId()
            } else {
                Team.valueOf(song.team).getTeamImageResourceId()
            }
            val imageUri = Uri.parse("android.resource://com.soi.moya/$resourceId")

            Utility.buildMediaItem(
                title = song.title,
                mediaId = song.songId,
                mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
                artist = Team.valueOf(song.team).getKrTeamName(),
                sourceUri = Uri.fromFile(file),
                imageUri = imageUri
            )
        }

        withContext(Dispatchers.Main) {
            player?.setMediaItems(mediaItems)
        }
    }

    //알림센터 위젯 설정
    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {
        @SuppressLint("ResourceType")
        override fun onForegroundServiceStartNotAllowedException() {
            if (
                Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
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
                        NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.notification_content_text))
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

    //최근 앱 목록에서 제거 시 호출
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        val player = mediaSession?.player
        if (player == null
            || !player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED
        ) stopSelf()

        mediaSession?.release()
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        val player = mediaSession?.player
        if (player == null
            || !player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED
        ) stopSelf()

        mediaSession?.release()
        getBackStackedActivity()?.let { mediaSession?.setSessionActivity(it) }
        player?.release()
        mediaSession?.release()
        clearListener()
        super.onDestroy()
    }

    private inner class MoyaMediaSessionCallback(
        private val context: Context
    ) : MediaSession.Callback {
        /*
        * MediaSession의 클라이언트인 MediaController가 MediaSession에 연결할때 호출되는 콜백 메서드로
        * 클라이언트가 세션에 연결될 때 어떤 명령어를 사용할 수 있는지를 정의함
        *
        * 명령어 집합 정의
        * 명령어 집합 반환
        */

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

        /* 미디어세션에서 다시 재생 시작 시 호출
        * 재생 중단 후 다시 재생할 시점에 어떤 플레이리스트를 로드할지를 결정
        *
         */
        @UnstableApi
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            val settable = SettableFuture.create<MediaItemsWithStartPosition>()
            scope.launch {
                try {
                    val mediaItems =
                        getCurrentMediaItemsFromSession(mediaSession) ?: loadPlaylist(application)
                    restoreMediaSession(application, mediaSession)
                    settable.set(MediaItemsWithStartPosition(mediaItems, 0, 0))
                } catch (e: Exception) {
                    settable.setException(e)
                }
            }
            return settable
        }
        fun getCurrentMediaItemsFromSession(mediaSession: MediaSession): List<MediaItem>? {
            val player = mediaSession.player
            return if (player.mediaItemCount > 0) {
                val mediaItems = mutableListOf<MediaItem>()
                for (i in 0 until player.mediaItemCount) {
                    mediaItems.add(player.getMediaItemAt(i))
                }
                mediaItems
            } else {
                null  // 세션에 재생 목록이 없을 경우
            }
        }

        @SuppressLint("Range")
        @OptIn(UnstableApi::class)
        suspend fun loadPlaylist(application: MoyaApplication): List<MediaItem> {
            val playlist = withContext(Dispatchers.IO) {
                application.container.itemsRepository.getByDefaultPlaylist()
                    .firstOrNull() ?: emptyList()
            }
            val filePath = application.filesDir.absolutePath
            val mediaItems = playlist.mapNotNull { song ->
                val file = File(filePath, "${song.songId}-${song.title}.mp3")
                if (!file.exists()) return@mapNotNull null

                val resourceId = if (song.type) {
                    Team.valueOf(song.team).getPlayerAlbumImageResourceId()
                } else {
                    Team.valueOf(song.team).getTeamImageResourceId()
                }
                val imageUri = Uri.parse("android.resource://com.soi.moya/$resourceId")

                Utility.buildMediaItem(
                    title = song.title,
                    mediaId = song.songId,
                    mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
                    artist = Team.valueOf(song.team).getKrTeamName(),
                    sourceUri = Uri.fromFile(file),
                    imageUri = imageUri
                )
            }

            return mediaItems
        }

        @OptIn(UnstableApi::class)
        suspend fun restoreMediaSession(application: MoyaApplication, mediaSession: MediaSession) {
            val mediaItems = getCurrentMediaItemsFromSession(mediaSession)

            if (mediaItems != null && mediaItems.isNotEmpty()) {
                mediaSession.player.setMediaItems(mediaItems)
            } else {
                val newMediaItems = loadPlaylist(application)
                mediaSession.player.setMediaItems(newMediaItems)
            }
            mediaSession.player.prepare()
        }

        private fun resolveMediaItems(mediaItems: List<MediaItem>): List<MediaItem> {
            val currentPlaylist = mutableListOf<MediaItem>()

            player?.let {
                for (i in 0 until it.mediaItemCount) {
                    currentPlaylist.add(it.getMediaItemAt(i))
                }
            }
            currentPlaylist.addAll(mediaItems)

            return currentPlaylist
        }
    }
}

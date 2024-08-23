package com.soi.moya.playback

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaButtonReceiver
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.soi.moya.R
import com.soi.moya.ui.main_activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

open class MoyaPlaybackService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "moya_session_notification_channel_id"
        private const val CHANNEL_NAME = "Moya Media Playback"
        const val SAVE_TO_FAVORITES = "com.moya.save_to_favorites"
    }

    override fun onGetSession(controllerInfo: ControllerInfo): MediaSession? = mediaSession

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()

        /*
        *Android의 Media3 라이브러리에서 제공하는 추상 클래스
        * Player인터페이스를 구현하는 여러 플레이어 객체를 감싸서 다양한 플레이어에 대한 공통된 기능을 제공하는데 사용 */
        val forwardingPlayer = @OptIn(UnstableApi::class)
        object : ForwardingPlayer(player) {
            override fun play() {
                // Add custom logic
                super.play()
            }

            override fun setPlayWhenReady(playWhenReady: Boolean) {
                // Add custom logic
                super.setPlayWhenReady(playWhenReady)
            }
        }

        mediaSession = MediaSession.Builder(this, forwardingPlayer)
            .setCallback(MoyaMediaSessionCallback(this))
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val player = mediaSession?.player!!
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession.release()
        }
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
        override fun onConnect(
            session: MediaSession,
            controller: ControllerInfo
        ): ConnectionResult {
            //TODO: 공식문서 보고 커스텀 함수 적용하기
            val playerCommands =
                ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                    .remove(COMMAND_SEEK_TO_PREVIOUS)
                    .remove(COMMAND_SEEK_TO_NEXT)
                    .build()

            val sessionCommands = ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .add(SessionCommand(SAVE_TO_FAVORITES, Bundle.EMPTY))
                .build()

            return ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .setAvailablePlayerCommands(playerCommands)
                .build()
        }

        @OptIn(UnstableApi::class)
        override fun onCustomCommand(
            session: MediaSession,
            controller: ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return when (customCommand.customAction) {
                SAVE_TO_FAVORITES -> {
                    // Do custom logic here
                    saveToFavorites(session.player.currentMediaItem)
                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                else -> {
                    // Handle other custom commands or provide a default result
                    Futures.immediateFuture(SessionResult(SessionError.ERROR_UNKNOWN))
                }
            }
        }

        @UnstableApi
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            val settable = SettableFuture.create<MediaItemsWithStartPosition>()
            scope.launch {
                val resumptionPlaylist = restorePlaylist()
                settable.set(resumptionPlaylist)
            }
            return settable
        }

        private fun saveToFavorites(mediaItem: MediaItem?) {
            if (mediaItem == null) {
                Log.e("CustomMediaSessionCallback", "MediaItem is null, cannot save to favorites.")
                return
            }

            // Example: Extract media item information
            val mediaId = mediaItem.mediaId ?: return
            val mediaTitle = mediaItem.mediaMetadata.title ?: "Unknown Title"

            // Example: Save to a simple list or a database
            // Here, we will use a local list for demonstration purposes
            val favoritesList = getFavoritesList()

            if (favoritesList.contains(mediaId)) {
                Log.i("CustomMediaSessionCallback", "Media item is already in favorites.")
            } else {
                favoritesList.add(mediaId)
                Log.i("CustomMediaSessionCallback", "Media item added to favorites: $mediaTitle")
                // Optionally, save the updated list to persistent storage
                saveFavoritesList(favoritesList)
            }
        }

        // Example function to get the current favorites list
        private fun getFavoritesList(): MutableList<String> {
            // Example: Load from shared preferences or a database
            // Here, we will use a simple mutable list
            return mutableListOf()
        }

        // Example function to save the updated favorites list
        private fun saveFavoritesList(favoritesList: List<String>) {
            // Example: Save to shared preferences or a database
            // Here, we will just log the action
            Log.i("CustomMediaSessionCallback", "Favorites list updated: $favoritesList")
        }


        //TODO: 재생목록 가져오는 로직 추가하기
        @OptIn(UnstableApi::class)
        private suspend fun restorePlaylist(): MediaItemsWithStartPosition {
            return withContext(Dispatchers.IO) {
                // Retrieve the playlist and start position from SharedPreferences
                val playlistJson = "[]"
                val startPosition = 0

                val mediaItems = mutableListOf<MediaItem>()
                val jsonArray = JSONArray(playlistJson)
                for (i in 0 until jsonArray.length()) {
                    val mediaItemJson = jsonArray.getJSONObject(i)
                    val uri = mediaItemJson.getString("uri")
                    val mediaId = mediaItemJson.getString("mediaId")
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMediaId(mediaId)
                        .build()
                    mediaItems.add(mediaItem)
                }
                MediaItemsWithStartPosition(mediaItems, startPosition, 0)
            }
        }
    }
}

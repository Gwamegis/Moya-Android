//package com.soi.moya.playback
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Intent
//import android.os.Build
//import android.support.v4.media.session.MediaSessionCompat
//import androidx.annotation.OptIn
//import androidx.core.app.NotificationCompat
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.session.MediaSession
//import androidx.media3.session.MediaSessionService
//import com.soi.moya.R
//import com.soi.moya.ui.main_activity.MainActivity
//class MoyaSessionPlaybackService : MediaSessionService() {
//    companion object {
//        const val CHANNEL_ID = "moya_media_playback_channel"
//        const val NOTIFICATION_ID = 1
//        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
//        const val ACTION_TOGGLE_PLAYBACK = "ACTION_TOGGLE_PLAYBACK"
//        const val ACTION_NEXT = "ACTION_NEXT"
//    }
//
//    private lateinit var mediaSession: MediaSession
//    private lateinit var exoPlayer: ExoPlayer
//
//    override fun onCreate() {
//        super.onCreate()
//
//        exoPlayer = ExoPlayer.Builder(this).build()
//        mediaSession = MediaSession.Builder(this, exoPlayer).build()
//
//        createNotificationChannel()
//
//        val notification = createMediaNotification()
//        startForeground(NOTIFICATION_ID, notification)
//    }
//
//    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
//        return mediaSession
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaSession.release()
//        exoPlayer.release()
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Media Playback",
//                NotificationManager.IMPORTANCE_LOW
//            ).apply {
//                description = "Media playback controls"
//            }
//
//            val notificationManager: NotificationManager =
//                getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun createMediaNotification(): Notification {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val playPauseIcon = if (exoPlayer.isPlaying) R.drawable.pause_fill else R.drawable.play_arrow
//
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("모두의 야구")
//            .setContentText("music title")
//            .setSmallIcon(R.drawable.alarm)
//            .setContentIntent(pendingIntent)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .addAction(R.drawable.skip_previous, "Previous", getActionIntent(ACTION_PREVIOUS))
//            .addAction(playPauseIcon, "Play/Pause", getActionIntent(ACTION_TOGGLE_PLAYBACK))
//            .addAction(R.drawable.skip_next, "Next", getActionIntent(ACTION_NEXT))
//            .setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(mediaSession.sessionCompatToken)  // MediaSession의 sessionToken 사용
//                    .setShowActionsInCompactView(1) // Play/Pause action
//            )
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .build()
//    }
//
//    private fun getActionIntent(action: String): PendingIntent {
//        val intent = Intent(this, MoyaSessionPlaybackService::class.java).apply {
//            this.action = action
//        }
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        intent?.action?.let {
//            when (it) {
//                ACTION_PREVIOUS -> exoPlayer.seekToPreviousMediaItem()
//                ACTION_TOGGLE_PLAYBACK -> if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
//                ACTION_NEXT -> exoPlayer.seekToNextMediaItem()
//            }
//        }
//        return START_STICKY
//    }
//}

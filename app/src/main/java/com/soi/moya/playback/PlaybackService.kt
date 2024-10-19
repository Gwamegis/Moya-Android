package com.soi.moya.playback

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.soi.moya.ui.main_activity.MainActivity

class PlaybackService() : MoyaPlaybackService() {
    companion object {
        private const val immutableFlag = PendingIntent.FLAG_IMMUTABLE
    }

    override fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            Log.d("**stop service", "stop service getBackStackedActivity")
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun stopService(name: Intent?): Boolean {
        Log.d("**stop service", "stop service PlaybackService")
        stopSelf()
        return super.stopService(name)
    }

    override fun onDestroy() {
        Log.d("**stop service", "onDestroy PlaybackService")
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
        super.onDestroy()
    }
}

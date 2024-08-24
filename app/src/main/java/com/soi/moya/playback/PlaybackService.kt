package com.soi.moya.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import com.soi.moya.ui.MoyaApplication
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
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
//            addNextIntent(Intent(this@PlaybackService, PlayerActivity::class.java))
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}

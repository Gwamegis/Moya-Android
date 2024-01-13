package com.soi.moya.domain

import android.content.Context
import android.media.MediaPlayer
import com.soi.moya.R
import java.lang.Exception

class MusicPlayerManager(private val context: Context) {
    private val player = MediaPlayer()

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun playMusic() {
        try {
            val afd = context.resources.openRawResourceFd(R.raw.test)
            player.reset()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepareAsync()
            player.setOnPreparedListener {
                player.start()
            }
            afd.close()
        } catch (e: Exception) {
            // 에러 처리
        }
    }

    fun pauseMusic() {
        if (player.isPlaying) {
            player.pause()
        }
    }

    fun resumeMusic() {
        if (!player.isPlaying) {
            player.start()
        }
    }

    fun stopMusic() {
        if (player.isPlaying) {
            player.stop()
        }
    }
}

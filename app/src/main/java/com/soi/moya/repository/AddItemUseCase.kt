package com.soi.moya.repository

import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.media3.common.MediaItem
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.BaseMusic
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.toDefaultItem
import com.soi.moya.models.toItem
import com.soi.moya.models.toMediaItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.ui.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val mediaControllerManager: MediaControllerManager,
) {
    suspend fun handleExistingMusic(
        existingMusic: StoredMusic,
        mediaItem: MediaItem,
        count: Int
    ): Int {
        val existingIndex = mediaControllerManager.mediaItemList.value.indexOfFirst { it.mediaId == mediaItem.mediaId }

        Log.d("**AddItemUseCase-exist", existingMusic.title + " " + existingIndex.toString())
        storedMusicRepository.updateOrder(
            start = existingIndex,
            end = count,
            increment = -1
        )
        storedMusicRepository.updateOrder(existingMusic.songId, count - 1)

        withContext(Dispatchers.Main) {
            if (existingIndex != null) {
                mediaControllerManager.controller?.removeMediaItem(existingIndex)
            }
            mediaControllerManager.controller?.addMediaItem(mediaItem)
            mediaControllerManager.addMediaItem(mediaItem)
        }

        return count - 1
    }
    suspend fun handleNewMusic(music: BaseMusic, mediaItem: MediaItem, count: Int): Int {
        withContext(Dispatchers.Main) {
            mediaControllerManager.controller?.addMediaItem(mediaItem)
            mediaControllerManager.addMediaItem(mediaItem)
        }
        Log.d("**AddItemUseCase-new", music.title + " ")

        val newMusic = when (music) {
            is MusicInfo -> {
                music.toStoredMusic(
                    team = music.team,
                    order = count,
                    date = Utility.getCurrentTimeString(),
                    playlist = "default"
                )
            }
            is StoredMusic -> {
                music.toDefaultItem(
                    playlist = "default",
                    order = count,
                    date = Utility.getCurrentTimeString()
                )
            }
            else -> throw IllegalArgumentException("Unknown music type")
        }
        storedMusicRepository.insertItem(newMusic.toItem())

        return count
    }
}
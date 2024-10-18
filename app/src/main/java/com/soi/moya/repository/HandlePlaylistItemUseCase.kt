package com.soi.moya.repository

import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.BaseMusic
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.toDefaultItem
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.ui.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HandlePlaylistItemUseCase @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository,
    private val mediaControllerManager: MediaControllerManager,
) {
    suspend fun handleExistingMusic(
        existingMusic: StoredMusic,
        mediaItem: MediaItem,
        count: Int
    ): Int {
        val existingIndex = mediaControllerManager.mediaItemList.value.indexOfFirst { it.mediaId == mediaItem.mediaId }

        storedMusicRepository.updateOrder(
            start = existingIndex,
            end = count,
            increment = -1
        )
        storedMusicRepository.updateOrder(existingMusic.songId, count - 1)

        withContext(Dispatchers.Main) {
            mediaControllerManager.controller?.removeMediaItem(existingIndex)
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

    suspend fun removePlaylistItem(songId: String, order: Int, count: Int) {
        val controller = mediaControllerManager.controller
        val index = mediaControllerManager.mediaItemList.value.indexOfFirst { it.mediaId == songId }

        if (index != -1) {
            withContext(Dispatchers.Main) {
                mediaControllerManager.removeMediaItem(index)
            }
        }
        withContext(Dispatchers.IO) {
            storedMusicRepository.deleteById(id = songId, playlist = "default")
            storedMusicRepository.updateOrder(start = order + 1, end = count, increment = -1)
        }
    }
}
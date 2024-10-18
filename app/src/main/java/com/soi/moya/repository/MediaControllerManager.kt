package com.soi.moya.repository

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.soi.moya.playback.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaControllerManager @Inject constructor(
    private val application: Application,
    private val musicStateRepository: MusicStateRepository
) {
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    val controller: MediaController?
        get() = if (controllerFuture.isDone && !controllerFuture.isCancelled) controllerFuture.get() else null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _mediaItemList = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItemList: StateFlow<List<MediaItem>> = _mediaItemList

    init {
        initializeController()
    }

    private fun initializeController() {
        Log.e("**initializeController", "initializeController")

        controllerFuture = MediaController.Builder(
            application,
            SessionToken(application, ComponentName(application, PlaybackService::class.java))
        ).buildAsync()

        controllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }
    private fun setController() {
        Log.e("**setController", "setController")

        val controller = this.controller ?: return
        // Controller 설정 및 이벤트 리스너 등록
        initializeMediaList()

        controller.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                    }
                    if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                    }
                    if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                        updateMediaMetadataUI()
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    saveCurrentSong(songId = mediaItem?.mediaId ?: "")
                }
            }
        )
    }

    private fun saveCurrentSong(songId: String) {
        musicStateRepository.setCurrentPlaySongId(songId)
        musicStateRepository.setCurrentPlaySongPosition(getCurrentMediaItemIndex())
    }

    private fun getCurrentMediaItemIndex(): Int {
        return controller?.currentMediaItemIndex ?: -1
    }
    fun initializeMediaList() {
        val controller = this.controller ?: return
        clearMediaItemList()
        for (i in 0 until controller.mediaItemCount) {
            addMediaItem(controller.getMediaItemAt(i))
        }
    }

    @OptIn(UnstableApi::class)
    fun updateMediaList(newList: List<MediaItem>) {
        _mediaItemList.value = newList
        controller?.setMediaItems(_mediaItemList.value)
    }

    fun clearMediaItemList() {
        _mediaItemList.value = emptyList()
    }
    fun addMediaItem(mediaItem: MediaItem) {
        val currentList = _mediaItemList.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.mediaId == mediaItem.mediaId }

        if (existingIndex != -1 ){
            currentList.removeAt(existingIndex)
        }
        currentList.add(mediaItem)
        _mediaItemList.value = currentList
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

    fun findMediaItemIndex(controller: MediaController, songId: String): Int {
        val mediaItemCount = controller.mediaItemCount
        for (index in 0 until mediaItemCount) {
            val mediaItem = controller.getMediaItemAt(index)
            if (mediaItem.mediaId == songId) { // mediaId가 songId와 일치하는지 확인
                return index
            }
        }
        return -1 // 아이템을 찾지 못한 경우
    }

    fun releaseController() {
        controller?.release()
    }
}

package com.soi.moya.repository

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.soi.moya.playback.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaControllerManager @Inject constructor(private val application: Application) {
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
        controllerFuture = MediaController.Builder(
            application,
            SessionToken(application, ComponentName(application, PlaybackService::class.java))
        ).buildAsync()

        controllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }
    @OptIn(UnstableApi::class) // PlayerView.setShowSubtitleButton
    private fun setController() {
        val controller = this.controller ?: return
        // Controller 설정 및 이벤트 리스너 등록
        initializeMediaList()

        controller.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                    }
                    if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                        initializeMediaList()
                    }
                    if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                        updateMediaMetadataUI()
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        // Trigger adapter update to change highlight of current item.
                        // 리스트 변경되어야한다고 알리기
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    // 미디어 아이템이 변경될 때 SharedPreferences 업데이트
//                    Log.e("***Music player manager", "position: ${getCurrentMediaItemIndex()}  id: ${mediaItem?.mediaId ?: ""}")
//                    saveCurrentSong(songId = mediaItem?.mediaId ?: "")
                }
            }
        )
    }
    private fun initializeMediaList() {
        val controller = this.controller ?: return
        clearMediaItemList()
        for (i in 0 until controller.mediaItemCount) {
            addMediaItem(controller.getMediaItemAt(i))
        }
    }

    fun clearMediaItemList() {
        _mediaItemList.value = emptyList()
    }
    fun addMediaItem(mediaItem: MediaItem) {
        val currentList = _mediaItemList.value
        val updatedList = currentList.toMutableList().apply {
            add(mediaItem)
        }
        _mediaItemList.value = updatedList
    }
    fun addMediaItems(mediaItems: List<MediaItem>) {
        // 현재 상태를 가져옵니다.
        val currentList = _mediaItemList.value
        val updatedList = currentList.toMutableList().apply {
            addAll(mediaItems)
        }
        _mediaItemList.value = updatedList
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

    fun releaseController() {
        controller?.release()
    }
}

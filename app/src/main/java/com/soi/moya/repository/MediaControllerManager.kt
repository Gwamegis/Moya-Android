package com.soi.moya.repository

import android.app.Application
import android.content.ComponentName
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.soi.moya.playback.PlaybackService
import javax.inject.Inject

class MediaControllerManager @Inject constructor(private val application: Application) {
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    val controller: MediaController?
        get() = if (controllerFuture.isDone && !controllerFuture.isCancelled) controllerFuture.get() else null

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

    private fun setController() {
        val controller = this.controller ?: return
        // Controller 설정 및 이벤트 리스너 등록
    }

//    private fun initializeMediaList() {
//        val controller = this.controller ?: return
//        clearMediaItemList()
//        for (i in 0 until controller.mediaItemCount) {
//            addMediaItem(controller.getMediaItemAt(i))
//        }
//    }
//
//    fun clearMediaItemList() {
//        _mediaItemList.value = emptyList()
//    }
//    fun addMediaItem(mediaItem: MediaItem) {
//        val currentList = _mediaItemList.value
//        val updatedList = currentList.toMutableList().apply {
//            add(mediaItem)
//        }
//        _mediaItemList.value = updatedList
//    }
//    fun addMediaItems(mediaItems: List<MediaItem>) {
//        // 현재 상태를 가져옵니다.
//        val currentList = _mediaItemList.value
//        val updatedList = currentList.toMutableList().apply {
//            addAll(mediaItems)
//        }
//        _mediaItemList.value = updatedList
//    }

    fun releaseController() {
        controller?.release()
    }
}

package com.soi.moya.ui

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class WindowSize {
    NORMAL, MINI, TABLET
}
object Utility {
    fun getDeviceType(context: Context): WindowSize {
        val display = context.resources.displayMetrics

        val deviceType =
            if(display == null) {
                WindowSize.NORMAL
            }else {
                // 태블릿, 폴드 펼침
                if(display.widthPixels > 1600) {
                    WindowSize.TABLET
                }
                // 미니, 폴드 닫힘
                else if(display.widthPixels < 980) {
                    WindowSize.MINI
                }
                // 일반
                else{
                    WindowSize.NORMAL
                }
            }

        return deviceType
    }
    fun getCurrentTimeString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean = true,
        isBrowsable: Boolean = false,
        mediaType: @MediaMetadata.MediaType Int,
        subtitleConfigurations: List<MediaItem.SubtitleConfiguration> = mutableListOf(),
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setIsBrowsable(isBrowsable)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .setMediaType(mediaType)
                .build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setSubtitleConfigurations(subtitleConfigurations)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

}
package com.soi.moya.models

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.soi.moya.ui.Utility
import java.io.File
import java.util.Date

@Entity(tableName = "stored_music")
data class StoredMusic(
    @PrimaryKey
    override val id: String,
    val songId: String,
    val team: String,
    override val title: String,
    override val lyrics: String,
    override val info: String,
    override val type: Boolean,
    override val url: String,
    val order: Int,
    val date: String,
    @ColumnInfo(name = "playlist_title") val playlist: String
) : BaseMusic

fun StoredMusic.toItem(): StoredMusic = StoredMusic(
    id = id,
    songId = songId,
    team = team,
    title = title,
    lyrics = lyrics,
    info = info,
    type = type,
    url = url,
    order = order,
    date = date,
    playlist = playlist
)

fun StoredMusic.copy(order: Int): StoredMusic = StoredMusic(
    id = id,
    songId = songId,
    team = team,
    title = title,
    lyrics = lyrics,
    info = info,
    type = type,
    url = url,
    order = order,
    date = date,
    playlist = playlist
)

fun StoredMusic.toDefaultItem(playlist: String, order: Int, date: String): StoredMusic = StoredMusic(
    id = id+"_"+playlist,
    songId = songId,
    team = team,
    title = title,
    lyrics = lyrics,
    info = info,
    type = type,
    url = url,
    order = order,
    date = date,
    playlist = playlist
)

fun StoredMusic.toMusicInfo(): MusicInfo = MusicInfo(
    id = songId,
    team = Team.fromString(team) ?: Team.doosan,
    info = info,
    lyrics = lyrics,
    title = title,
    url = url,
    type = type
)

fun StoredMusic.toMediaItem(filePath: String): MediaItem {
    val file = File(filePath, "${id}-${title}.mp3")

    val resourceId = if(type) {
        Team.valueOf(team).getPlayerAlbumImageResourceId()
    } else {
        Team.valueOf(team).getTeamImageResourceId()
    }
    val imageUri = Uri.parse("android.resource://com.soi.moya/$resourceId")

    return Utility.buildMediaItem(
        title = title,
        mediaId = id,
        mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
        artist = Team.valueOf(team).getKrTeamName(),
        sourceUri = Uri.fromFile(file),
        imageUri = imageUri
    )
}
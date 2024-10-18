package com.soi.moya.models

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.soi.moya.ui.Utility
import java.io.File

interface BaseMusic {
    val id: String
    val title: String
    val lyrics: String
    val info: String
    val type: Boolean
    val url: String
}
data class Music(
    override val id: String = "",
    override val info: String = "",
    override val lyrics: String = "",
    override val title: String = "",
    override val type: Boolean = false,
    override val url: String = ""
) : BaseMusic

data class MusicInfo(
    override val id: String = "",
    override val info: String = "",
    override val lyrics: String = "",
    override val title: String = "",
    override val type: Boolean = false,
    val team: Team = Team.doosan,
    override val url: String = ""
) : BaseMusic

fun Music.toMusicInfo(team: Team): MusicInfo = MusicInfo(
    id = id,
    team = team,
    title = title,
    lyrics = lyrics,
    info = info,
    type = type,
    url = url
)

fun MusicInfo.toStoredMusic(team: Team, order: Int, date: String, playlist: String): StoredMusic = StoredMusic(
    id = id+"_"+playlist,
    songId = id,
    team = team.name,
    title = title,
    lyrics = lyrics,
    info = info,
    type = type,
    url = url,
    order = order,
    date = date,
    playlist = playlist
)

fun MusicInfo.toMediaItem(filePath: String): MediaItem {
    val file = File(filePath, "${id}-${title}.mp3")

    val resourceId = if(type) {
        team.getPlayerAlbumImageResourceId()
    } else {
        team.getTeamImageResourceId()
    }
    val imageUri = Uri.parse("android.resource://com.soi.moya/$resourceId")

    return Utility.buildMediaItem(
        title = title,
        mediaId = id,
        mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
        artist = team.getKrTeamName(),
        sourceUri = Uri.fromFile(file),
        imageUri = imageUri
    )
}
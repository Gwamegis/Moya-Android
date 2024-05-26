package com.soi.moya.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "stored_music")
data class StoredMusic(
    @PrimaryKey
    val id: String,
    val songId: String,
    val team: String,
    val title: String,
    val lyrics: String,
    val info: String,
    val type: Boolean,
    val url: String,
    val order: Int,
    val date: String,
    @ColumnInfo(name = "playlist_title") val playlist: String
)

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

fun StoredMusic.copy(playlist: String, order: Int, date: String): StoredMusic = StoredMusic(
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
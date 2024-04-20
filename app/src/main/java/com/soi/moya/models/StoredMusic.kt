package com.soi.moya.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stored_music")
data class StoredMusic(
    @PrimaryKey
    val id: String,
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
    id = id,
    team = Team.fromString(team) ?: Team.doosan,
    info = info,
    lyrics = lyrics,
    title = title,
    url = url,
    type = type
)
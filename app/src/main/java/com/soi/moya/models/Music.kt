package com.soi.moya.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Music(
    val id: String = "",
    val info: String = "",
    val lyrics: String = "",
    val title: String = "",
    val type: Boolean = false,
    val url: String = ""
)

@Entity(tableName = "music_info")
data class MusicInfo(
    @PrimaryKey val id: String = "",
    val info: String = "",
    val lyrics: String = "",
    val title: String = "",
    val type: Boolean = false,
    val team: Team = Team.doosan,
    val url: String = ""
)

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
    id = id,
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
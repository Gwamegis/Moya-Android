package com.soi.moya.models

data class Music(
    val id: String = "",
    val info: String = "",
    val lyrics: String = "",
    val title: String = "",
    val type: Boolean = false,
    val url: String = ""
)

fun Music.toStoredMusic(team: Team, order: Int, date: String, playlist: String): StoredMusic = StoredMusic(
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
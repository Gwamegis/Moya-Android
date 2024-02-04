package com.soi.moya.repository

import com.soi.moya.models.Music
import com.soi.moya.util.UiState

interface MusicRepository {
    fun getMusics(
        teamName: String,
        result: (UiState<List<Music>>) -> Unit,
    )
}

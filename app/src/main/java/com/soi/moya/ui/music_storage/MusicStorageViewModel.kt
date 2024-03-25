package com.soi.moya.ui.music_storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.Music
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MusicStorageViewModel(
    private val storedMusicRepository: StoredMusicRepository,
    application: Application
): AndroidViewModel(application = application) {
    val storageUiState: StateFlow<StorageUiState> =
        storedMusicRepository.getAllItemsStream().map { StorageUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = StorageUiState()
            )
    val startHeightNum = 200

    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: Map<String, LiveData<List<String>>> get() = _seasonSongManager.getSeasonSongs()

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun fetchAlbumImageResourceId(music: Music, team: Team): Int {
        return if (seasonSongs[team.name]?.value?.contains(music.title) == true) {
            team.getSeasonSongAlbumImageResourceId()
        } else {
            if(music.type) {
                team.getPlayerAlbumImageResourceId()
            } else {
                team.getTeamImageResourceId()
            }
        }
    }
}

data class StorageUiState(val itemList: List<StoredMusic> = listOf())
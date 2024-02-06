package com.soi.moya.ui.music_storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.StoredMusic
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
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class StorageUiState(val itemList: List<StoredMusic> = listOf())
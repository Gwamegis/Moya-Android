package com.soi.moya.ui.music_storage

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.toItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicStorageViewModel(
    private val storedMusicRepository: StoredMusicRepository
): AndroidViewModel(application = Application()) {
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

    //TODO: 추후 이동 및 삭제 (사용방법 참고)
    fun saveItem() {
        val testItem = StoredMusic(
            id = "3",
            team = "doosan",
            title = "music3",
            lyrics = "가나다라",
            info = "",
            type = false,
            url = "test",
            order = 1,
            date = "223101123",
            playlist = "favorite"
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.insertItem(testItem.toItem())
            }
        }
    }

    fun deleteItem(item: StoredMusic) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.deleteItem(item)
            }
        }
    }
}

data class StorageUiState(val itemList: List<StoredMusic> = listOf())
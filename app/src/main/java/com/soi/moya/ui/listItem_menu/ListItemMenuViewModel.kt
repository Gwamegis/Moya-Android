package com.soi.moya.ui.listItem_menu

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.ui.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListItemMenuViewModel @Inject constructor(
    private val storedMusicRepository: StoredMusicRepository
): ViewModel() {
    suspend fun doesItemExist(itemId: String): Boolean {
        return storedMusicRepository.doesItemExist(itemId = itemId, playlist = "favorite")
    }

    fun saveItem(music: MusicInfo, team: Team) {
        viewModelScope.launch {
            val order = storedMusicRepository.getItemCount(playlist = "favorite")
            val music = music.toStoredMusic(
                team = team,
                order = order,
                date = Utility.getCurrentTimeString(),
                playlist = "favorite"
            )
            withContext(Dispatchers.IO) {
                storedMusicRepository.insertItem(music.toItem())
            }
        }
    }
    fun deleteItem(music: MusicInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storedMusicRepository.deleteById(id = music.id, playlist = "favorite")
            }
        }
    }
}
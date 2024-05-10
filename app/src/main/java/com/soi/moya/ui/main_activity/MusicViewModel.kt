package com.soi.moya.ui.main_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.MusicManager
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.UserPreferences
import com.soi.moya.repository.MusicPlayerManager
import kotlinx.coroutines.launch

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val musicManager = MusicManager.getInstance()
    private val musicPlayerManager = MusicPlayerManager.getInstance(application = application)

    private val _userPreferences = UserPreferences(application)

    private val _selectedMusic = MutableLiveData<MusicInfo?>()
    val selectedMusic: LiveData<MusicInfo?> = _selectedMusic

    init {
        observeCurrentSongId()
    }

    private fun observeCurrentSongId() {
        viewModelScope.launch {
            // ...songId 변경 시 작업 작성
            _userPreferences.currentPlaySongId.collect{ songId ->
                if (songId != null) {
                    musicManager.getMusicById(songId)?.let { musicInfo ->
                        if (_selectedMusic.value != musicInfo) {
                            _selectedMusic.value = musicInfo
                            musicPlayerManager.playMusic(musicInfo)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: MusicViewModel? = null

        fun getInstance(application: Application) =
            instance ?: synchronized(this) {
                instance ?: MusicViewModel(application).also { instance = it }
            }
    }
}
package com.soi.moya.ui.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicStateRepository: MusicStateRepository
) : ViewModel() {

    val selectedTeam: LiveData<Team?> = musicStateRepository.selectedTeam.asLiveData()
    val currentPlaySongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()
    val isMiniplayerActivated: LiveData<Boolean> = musicStateRepository.isMiniPlayerActivated.asLiveData()
    val isNeedHideMiniplayer: LiveData<Boolean> = musicStateRepository.isNeedHideMiniPlayer.asLiveData()
    val isInitialLoad: LiveData<Boolean> = musicStateRepository.isInitialLoad.asLiveData()

    init {
        viewModelScope.launch {
            musicStateRepository.loadUserPreferences()
        }
    }
}
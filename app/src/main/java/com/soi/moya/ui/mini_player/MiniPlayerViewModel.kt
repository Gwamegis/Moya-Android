package com.soi.moya.ui.mini_player

import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.soi.moya.models.Team
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MiniPlayerViewModel @Inject constructor(
    private val musicStateRepository: MusicStateRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {
    val minHeight = 55f
    private val _maxHeight = MutableStateFlow(0f)

    val threshold = mutableFloatStateOf(0f)
    val scalingFactor = 0.4f
    val bottomPadding = 110f
    val horizontalPadding = 10f

    val isMiniPlayerActivated: LiveData<Boolean> = musicStateRepository.isMiniPlayerActivated.asLiveData()
    val selectedTeam: LiveData<Team?> = musicStateRepository.selectedTeam.asLiveData()


    fun setMaxHeight(value: Float) {
        _maxHeight.value = value
        threshold.value = value / 2
    }

    fun setIsMiniplayerActivated(isActivated: Boolean) {
        musicStateRepository.setMiniPlayerActivated(isActivated)
    }

    fun popBackStack() {
        musicStateRepository.setMiniPlayerActivated(true)
    }
    fun isMediaItemListNotEmpty(): Boolean {
        return mediaControllerManager.mediaItemList.value.isNotEmpty()
    }
}
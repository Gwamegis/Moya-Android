package com.soi.moya.ui.select_team

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.soi.moya.models.Team
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectTeamViewModel @Inject constructor(
    private val musicStateRepository: MusicStateRepository
): ViewModel() {
    val teams = Team.values()
    private val selectedTeam: LiveData<Team?> = musicStateRepository.selectedTeam.asLiveData()
    var selectTeam = mutableStateOf<Team?>(selectedTeam.value)


    fun onTeamSelected(currentTeam: Team) {
        if(selectTeam.value == currentTeam) {
            selectTeam.value = null
        } else {
            selectTeam.value = currentTeam
        }
    }
    fun onClickNext() {
        selectTeam.value?.let { musicStateRepository.setSelectedTeam(team = it) }
        musicStateRepository.setNeedHideMiniPlayer(false)
    }
}
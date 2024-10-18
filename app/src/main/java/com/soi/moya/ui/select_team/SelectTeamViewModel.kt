package com.soi.moya.ui.select_team

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.repository.MusicStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectTeamViewModel @Inject constructor(
    private val musicStateRepository: MusicStateRepository,
    private val userPreferences: UserPreferences
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
        musicStateRepository.setNeedHideMiniPlayer(false)
        viewModelScope.launch {
            selectTeam.value?.let { userPreferences.saveSelectedTeam(team = it) }
        }
    }
}
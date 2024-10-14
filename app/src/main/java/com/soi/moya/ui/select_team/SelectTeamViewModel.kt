package com.soi.moya.ui.select_team

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectTeamViewModel @Inject constructor(application: Application): ViewModel() {
    val teams = Team.values()
    var selectedTeam = mutableStateOf<Team?>(null)

    private val _userPreferences = UserPreferences(application)

    init {
        observeUserPreference()
    }

    private fun observeUserPreference() {
        viewModelScope.launch {
            _userPreferences.getSelectedTeam.collect { team ->
                selectedTeam.value = team?.let { Team.valueOf(it) }
            }
        }
    }

    fun onTeamSelected(currentTeam: Team) {
        if(selectedTeam.value == currentTeam) {
            selectedTeam.value = null
        } else {
            selectedTeam.value = currentTeam
        }
    }
    fun onClickNext() {
        viewModelScope.launch {
            selectedTeam.value?.let { _userPreferences.saveSelectedTeam(team = it) }
            _userPreferences.saveIsNeedHideMiniPlayer(isNeedToHide = false)
        }
    }
}
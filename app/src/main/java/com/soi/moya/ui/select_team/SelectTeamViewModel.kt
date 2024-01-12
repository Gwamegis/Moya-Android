package com.soi.moya.ui.select_team

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import kotlinx.coroutines.launch

class SelectTeamViewModel(application: Application): AndroidViewModel(application = Application()) {
    val teams = Team.values()
    var selectedTeam = mutableStateOf<Team?>(null)
    val userPreferences = UserPreferences(application)
    fun onTeamSelected(currentTeam: Team) {
        if(selectedTeam.value == currentTeam) {
            selectedTeam.value = null
        } else {
            selectedTeam.value = currentTeam
        }
    }
    fun onClickNext() {
        viewModelScope.launch {
            selectedTeam.value?.let { userPreferences.saveSelectedTeam(team = it) }
        }
        println("선택한 팀: ${selectedTeam.value}")
    }
}
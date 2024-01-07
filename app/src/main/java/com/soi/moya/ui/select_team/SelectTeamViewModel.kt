package com.soi.moya.ui.select_team

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.soi.moya.models.Team

class SelectTeamViewModel(): ViewModel() {
    val teams = Team.values()
    var selectedTeam = mutableStateOf<Team?>(null)

    fun onTeamSelected(currentTeam: Team) {
        if(selectedTeam.value == currentTeam) {
            selectedTeam.value = null
        } else {
            selectedTeam.value = currentTeam
        }
    }
    fun onClickNext() {
        println("선택한 팀: ${selectedTeam.value}")
    }
}
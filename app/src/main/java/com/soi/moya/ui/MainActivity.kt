package com.soi.moya.ui

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.ui.bottom_nav.BottomNavScreen
import com.soi.moya.ui.theme.MoyaTheme

class MainActivity : BaseComposeActivity() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val userPreferences = remember { UserPreferences(context) }
        val selectedTeam = userPreferences.getSelectedTeam.collectAsState(initial = "doosan").value

        MoyaTheme(team = Team.valueOf(selectedTeam ?: "doosan")) {
            BottomNavScreen()
        }
    }
}


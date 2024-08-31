package com.soi.moya.ui.main_activity

import android.os.Build
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.WindowMetricsCalculator
import com.soi.moya.R
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.data.MusicManager
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.ui.bottom_nav.BottomNavScreen
import com.soi.moya.ui.mini_player.MiniPlayerScreen
import com.soi.moya.ui.select_team.SelectTeamScreen
import com.soi.moya.ui.theme.MoyaTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : BaseComposeActivity() {

    private val musicManager = MusicManager.getInstance()

    private val toast: Toast by lazy {
        Toast.makeText(baseContext, getString(R.string.back_btn_pressed), Toast.LENGTH_SHORT)
    }
    private var backPressedTime: Long = 0
    private var _isMiniPlayerActivated = MutableStateFlow(false)

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val userPreferences = remember { UserPreferences(context) }
        val navController = rememberNavController()
        var selectedTeam by remember { mutableStateOf<String?>(null) }
        var isLoaded by remember { mutableStateOf(false) }
        var currentMusic by remember { mutableStateOf<MusicInfo?>(null) }
        var isNeedToHideMiniPlayer by remember { mutableStateOf(false) }

        LaunchedEffect(userPreferences.isMiniPlayerActivated) {
            userPreferences.isMiniPlayerActivated.collect { value ->
                _isMiniPlayerActivated.value = value
            }
        }

        LaunchedEffect(userPreferences.getSelectedTeam) {
            userPreferences.getSelectedTeam.collect { team ->
                selectedTeam = team
                isLoaded = true
            }
        }

        LaunchedEffect(userPreferences.isNeedHideMiniPlayer) {
            userPreferences.isNeedHideMiniPlayer.collect { value ->
                isNeedToHideMiniPlayer = value
            }
        }

        LaunchedEffect(userPreferences.currentPlaySongId) {
            userPreferences.currentPlaySongId.collect { id ->
                if (id != null) {
                    musicManager.getMusicById(id)?.let { musicInfo ->
                        currentMusic = musicInfo
                    }
                }
            }
        }

        if (isLoaded) {
            if (selectedTeam != null) {
                MoyaTheme(team = Team.valueOf(selectedTeam ?: "doosan")) {
                    BottomNavScreen()
                    currentMusic?.let { music ->
                        if (!isNeedToHideMiniPlayer) {
                            MiniPlayerScreen(
                                maxHeight = computeWindowSizeClasses(),
                                navController = navController,
                                music = music
                            )
                        }
                    }
                }
            } else {
                SelectTeamScreen(navController = navController)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setupOnBackPressedDispatcher()
        }
    }
    private fun computeWindowSizeClasses(): Float{
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val height = metrics.bounds.height()
        val density = resources.displayMetrics.density

        return height/density
    }


    private fun setupOnBackPressedDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleBackPress() {
        if (_isMiniPlayerActivated.value) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                toast.cancel()
                finish()
            } else {
                toast.show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        handleBackPress()
    }
}


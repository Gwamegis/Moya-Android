package com.soi.moya.ui.main_activity

import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.WindowMetricsCalculator
import com.soi.moya.R
import com.soi.moya.base.BaseComposeActivity
import com.soi.moya.data.MusicManager
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.bottom_nav.BottomNavScreen
import com.soi.moya.ui.mini_player.MiniPlayerScreen
import com.soi.moya.ui.select_team.SelectTeamScreen
import com.soi.moya.ui.theme.MoyaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {

    private val musicManager = MusicManager.getInstance()

    private val toast: Toast by lazy {
        Toast.makeText(baseContext, getString(R.string.back_btn_pressed), Toast.LENGTH_SHORT)
    }
    private var backPressedTime: Long = 0
    private var _isMiniPlayerActivated = MutableStateFlow(false)

    private val mainViewModel: MainViewModel by viewModels()

    private var backPressedCallback: OnBackPressedCallback? = null

    @Composable
    override fun Content() {

        val navController = rememberNavController()
        val selectedTeam = mainViewModel.selectedTeam.observeAsState().value
        var isLoaded = mainViewModel.isInitialLoad.observeAsState().value

        var currentMusic by remember { mutableStateOf<MusicInfo?>(null) }
        var isNeedToHideMiniPlayer by remember { mutableStateOf(false) }

        mainViewModel.currentPlaySongId.observeAsState().value?.let { songId ->
            musicManager.getMusicById(songId)?.let { musicInfo ->
                currentMusic = musicInfo
                Log.d("**mainActivity", "songID: $songId")
                Log.d("**mainActivity", "currentMusic: ${currentMusic!!.title}")
            }
        }
        mainViewModel.isMiniplayerActivated.observeAsState().value?.let { activated ->
            _isMiniPlayerActivated.value = !activated
        }
        mainViewModel.isNeedHideMiniplayer.observeAsState().value?.let { hide ->
            isNeedToHideMiniPlayer = hide
        }

        if (isLoaded == true) {
            if (selectedTeam != null) {
                MoyaTheme(team = selectedTeam) {
                    BottomNavScreen()
                    if (!isNeedToHideMiniPlayer) {
                        MiniPlayerScreen(
                            maxHeight = computeWindowSizeClasses(),
                            music = currentMusic
                        )
                    }
                }
            } else {
                SelectTeamScreen(navController = navController)
            }
        }
        BackHandler {
            handleBackPress()
        }
    }

    private fun computeWindowSizeClasses(): Float {
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val height = metrics.bounds.height()
        val density = resources.displayMetrics.density

        return height / density
    }

    private fun handleBackPress() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            toast.cancel()
            moveTaskToBack(true)
        } else {
            toast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}


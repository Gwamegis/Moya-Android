package com.soi.moya.ui.main_activity

import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
        // 초기 콜백 등록 여부를 기억하는 상태 변수
        val isBackPressedDispatcherSetup = remember { mutableStateOf(false) }

        // setupOnBackPressedDispatcher가 이미 호출되지 않았다면 호출
        if (!isBackPressedDispatcherSetup.value) {
            setupOnBackPressedDispatcher()
            isBackPressedDispatcherSetup.value = true
        }

        val navController = rememberNavController()
        val selectedTeam = mainViewModel.selectedTeam.observeAsState().value
        var isLoaded = mainViewModel.isInitialLoad.observeAsState().value

        var currentMusic by remember { mutableStateOf<MusicInfo?>(null) }
        var isNeedToHideMiniPlayer by remember { mutableStateOf(false) }

        mainViewModel.currentPlaySongId.observeAsState().value?.let { songId ->
            musicManager.getMusicById(songId)?.let { musicInfo ->
                currentMusic = musicInfo
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

        //TODO: 버전별 동작 확인하기
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            setupOnBackPressedDispatcher()
//        }
    }
    private fun computeWindowSizeClasses(): Float{
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val height = metrics.bounds.height()
        val density = resources.displayMetrics.density

        return height/density
    }


    private fun setupOnBackPressedDispatcher() {
        Log.d("**main", "main back")
        if (backPressedCallback == null) {
            backPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
            onBackPressedDispatcher.addCallback(this, backPressedCallback!!)
        }
    }
    private fun handleBackPress() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            toast.cancel()
            finish()
        } else {
            toast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        handleBackPress()
//    }
}


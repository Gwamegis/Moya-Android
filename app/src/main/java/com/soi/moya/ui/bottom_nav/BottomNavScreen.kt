package com.soi.moya.ui.bottom_nav

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.soi.moya.R
import com.soi.moya.data.MusicManager
import com.soi.moya.models.Team
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.MUSIC_LIST
import com.soi.moya.ui.MUSIC_STORAGE
import com.soi.moya.ui.SEARCH
import com.soi.moya.ui.music_list.MusicListScreen
import com.soi.moya.ui.music_storage.MusicStorageScreen
import com.soi.moya.ui.notice.NewFeatureNoticeScreen
import com.soi.moya.ui.notice.NewFeatureNoticeViewModel
import com.soi.moya.ui.notice.traffic.TrafficNoticeScreen
import com.soi.moya.ui.search.SearchScreen
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaTheme
import kotlinx.coroutines.launch

@Composable
fun BottomNavScreen() {
    val navController = rememberNavController()

    MusicManager.getInstance()

    NoticeBottomSheet {
        Scaffold(
            bottomBar = { BottomNav(navController = navController) }
        ) {
            Box(Modifier.padding(it)) {
                NavGraph(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoticeBottomSheet(
    content: @Composable () -> Unit,
) {
    val viewModel: NewFeatureNoticeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val isNotCheckedVersion = viewModel.isNotCheckedVersion
    val isExistTrafficIssue = viewModel.isExistTrafficIssue
    val version = viewModel.versionState
    val traffic = viewModel.traffic
    val activity = (LocalContext.current as? Activity)
    val scope = rememberCoroutineScope()
    val newFeatureSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val trafficSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(version.value, traffic.value) {
        if (isNotCheckedVersion.value) {
            scope.launch {
                newFeatureSheetState.show()
            }
        }
        if (isExistTrafficIssue.value) {
            scope.launch {
                trafficSheetState.show()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = newFeatureSheetState,
        sheetContent = {
//            if (isNotCheckedVersion.value) {
//                version.value?.let {
            NewFeatureNoticeScreen(
                version = version.value ?: return@ModalBottomSheetLayout,
                onDismissRequest = {
                    scope.launch {
                        val shouldTerminateApp = viewModel.checkRequiredUpdate()
                        if (shouldTerminateApp) {
                            activity?.finish()
                        }
                        newFeatureSheetState.hide()
                    }
                },
            )
        }
    ) {
        TrafficNoticeScreen(
            sheetState = trafficSheetState,
            traffic = traffic.value,
            content = content
        )
    }
}


@Composable
fun BottomNav(navController: NavHostController) {
    // TODO: select_team에서 선택한 값 사용 필요
    val selectedTeam: Team = Team.doosan
    val color = MoyaColor

    val items = listOf(
        NavItem.MusicList,
        NavItem.Search,
        NavItem.MusicStorage
    )

    BottomNavigation(
        backgroundColor = color.tabBarGray,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconID),
                        contentDescription = stringResource(id = item.labelID),
                        modifier = Modifier
                            .width(26.dp)
                            .height(26.dp)
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                label = {
                    Text(
                        stringResource(id = item.labelID),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                },
                selectedContentColor = MoyaTheme.colors.point,
                unselectedContentColor = color.darkGray
            )
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    //TODO: 선언 시점 변경 필요
    NavHost(navController = navController, startDestination = NavItem.MusicList.route) {
        // TODO: Screen 연결
        composable(NavItem.MusicList.route) {
            MusicListScreen()
        }
        composable(NavItem.Search.route) {
            SearchScreen()
        }
        composable(NavItem.MusicStorage.route) {
            MusicStorageScreen()
        }
    }
}

sealed class NavItem(val labelID: Int, val iconID: Int, val route: String) {
    object MusicList : NavItem(R.string.music_list, R.drawable.navigation_icon_home, MUSIC_LIST)
    object Search : NavItem(R.string.search, R.drawable.navigation_icon_search, SEARCH)
    object MusicStorage :
        NavItem(R.string.music_storage, R.drawable.navigation_icon_storage, MUSIC_STORAGE)
}

@Preview
@Composable
fun BottomNavScreenPreview() {
    BottomNavScreen()
}
package com.soi.moya.ui.bottom_nav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.soi.moya.R
import com.soi.moya.models.Team
import com.soi.moya.ui.MUSIC_LIST
import com.soi.moya.ui.MUSIC_PlAYER
import com.soi.moya.ui.MUSIC_STORAGE
import com.soi.moya.ui.SEARCH
import com.soi.moya.ui.SELECT_TEAM
import com.soi.moya.ui.music_player.MusicPlayerScreen
import com.soi.moya.ui.music_storage.MusicStorageScreen
import com.soi.moya.ui.search.SearchScreen
import com.soi.moya.ui.select_team.SelectTeamScreen
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.music_list.MusicListScreen as MusicListScreen
import com.soi.moya.ui.theme.MoyaTheme

@Composable
fun BottomNavScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = remember(navBackStackEntry) { navBackStackEntry?.destination?.route }

    Scaffold(
        bottomBar = {
            if (currentRoute != MUSIC_PlAYER && currentRoute != SELECT_TEAM) {
                BottomNav(navController = navController)
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            NavGraph(navController = navController)
        }
    }
}

// TODO: 삭제
@Composable
fun TestScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Main Screen", fontSize = 24.sp)
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
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

    NavHost(navController = navController, startDestination = NavItem.MusicList.route) {
        // TODO: Screen 연결
        composable(NavItem.MusicList.route) {
            MusicListScreen(
                navController = navController
            )
        }
        composable(NavItem.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(NavItem.MusicStorage.route) {
            MusicStorageScreen(navController = navController)
        }
        composable(MUSIC_PlAYER) { backStackEntry ->
            MusicPlayerScreen(
                navController = navController,
                songId = backStackEntry.arguments?.getString("songId")
            )
        }
        composable(SELECT_TEAM) {
            SelectTeamScreen(
                navController = navController
            )
        }
    }
}
sealed class NavItem(@StringRes val labelID: Int, val iconID: Int, val route: String) {
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
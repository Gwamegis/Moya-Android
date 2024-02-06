package com.soi.moya.ui.music_list

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.soi.moya.R
import com.soi.moya.models.Music
import com.soi.moya.models.Team
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.SELECT_TEAM
import com.soi.moya.ui.component.MusicListItem
import com.soi.moya.ui.component.RequestMusicButton
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicListScreen(
    viewModel: MusicListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
) {
    val selectedTeam = Team.doosan
    val scope = rememberCoroutineScope()
    val tabs = listOf(R.string.team_tab, R.string.player_tab)
    val pagerState = rememberPagerState(pageCount = {
        tabs.size
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MoyaColor.background)
    ) {
        Column {
            SwitchTeamAndPlayerTitleView(
                team = selectedTeam,
                tabs = tabs,
                state = pagerState,
                onTabSelected = { selectedTab ->
                    scope.launch {
                        pagerState.animateScrollToPage(selectedTab)
                    }
                })

            MusicListHeaderView(
                team = selectedTeam,
                musicListSize = viewModel.getMusicListSize(page = pagerState.currentPage),
                navController = navController
            )

            HorizontalPager(state = pagerState) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn() {

                        items(viewModel.getMusicListSize(page = page)) { index ->
                            MusicListItemView(
                                music = viewModel.getMusicAt(page = page, index = index),
                                navController = navController
                            )
                        }

                        item {
                            RequestMusicButtonView(color = selectedTeam.getPointColor())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwitchTeamAndPlayerTitleView(
    team: Team, tabs: List<Int>, state: PagerState, onTabSelected: (selectedTab: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Team.doosan.getSubColor())
            .padding(top = 60.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            tabs.forEachIndexed { index, tab ->
                Box(modifier = Modifier) {
                    Column {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(100.dp)
                                .height(40.dp)
                                .clickable {
                                    onTabSelected(index)
                                }
                        ) {
                            Text(
                                text = stringResource(id = tab),
                                style = getTextStyle(style = MoyaFont.CustomTitleBold),
                                color = if (state.currentPage == index) MoyaColor.white else {
                                    MoyaColor.unhighlightedWhite
                                }
                            )
                        }

                        if (state.currentPage == index) {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .background(team.getPointColor())
                                    .width(96.dp)
                                    .height(3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListHeaderView(
    team: Team,
    musicListSize: Int,
    navController: NavHostController
) {
    Column() {
        Box(
            modifier = Modifier
                .height(130.dp)
                .background(MoyaColor.white)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(team.getSubColor())
            )

            Image(painter = painterResource(id = R.drawable.main_banner_doosan),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        navController.navigate(SELECT_TEAM)
                    })
        }

        Text(
            text = stringResource(R.string.count_of_song, musicListSize),
            style = getTextStyle(style = MoyaFont.CustomCaptionBold),
            color = MoyaColor.darkGray,
            modifier = Modifier
                .padding(start = 20.dp)
                .padding(vertical = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListItemView(music: Music, navController: NavHostController) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    Divider()
    MusicListItem(
        music = music,
        onClickCell = {
            navController.navigate("MUSIC_PLAYER/${music.id}")
        },
        onClickExtraButton = {
            Log.d("clicked", "extra button")
            showBottomSheet = true
        },
        buttonImageResourceId = R.drawable.ellipse
    )
    if (showBottomSheet) {
        Divider(color = MoyaColor.gray, thickness = 1.dp)
        //TODO: 팀정보 연결
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(12.dp),
            containerColor = MoyaColor.background,
            dragHandle = {},
            windowInsets = WindowInsets.navigationBars
        ) {
            Box(modifier = Modifier.navigationBarsPadding()) {
                MainMenuScreen(music = music, team = Team.doosan)
            }
        }
    }
}

@Composable
fun RequestMusicButtonView(color: Color) {
    Divider()
    Spacer(modifier = Modifier.size(40.dp))
    Text(
        text = stringResource(id = R.string.request_music_info),
        style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        textAlign = TextAlign.Center
    )
    RequestMusicButton(color = color)
    Spacer(modifier = Modifier.size(80.dp))
}

@Preview
@Composable
fun MusicListScreenPreview() {
    val viewModel: MusicListViewModel = viewModel()
    val navController = rememberNavController()
    MusicListScreen(viewModel = viewModel, navController = navController)
}
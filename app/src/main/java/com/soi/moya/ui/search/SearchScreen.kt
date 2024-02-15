package com.soi.moya.ui.search

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.Utility
import com.soi.moya.ui.WindowSize
import com.soi.moya.ui.component.RequestMusicButton
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
) {
    val result by viewModel.searchResult.collectAsState()
    val text by viewModel.searchText.collectAsState()

    Column {
        SearchBar(viewModel = viewModel)

        if (text.isEmpty()) {
            InfoView()
        } else if (result.isEmpty()) {
            EmptyView()
        } else {
            ResultView(
                result = result,
                navController = navController
            )
        }
    }
}

@Composable
fun ResultView(result: List<MusicInfo>, navController: NavHostController) {
    LazyColumn {
        items(result) { music ->
            listItem(music = music) {
                navController.navigate("MUSIC_PLAYER/${music.team.name}/${music.id}")
            }
        }
    }
}

@Composable
fun InfoView() {
    val context = LocalContext.current
    val imageSize = if (Utility.getDeviceType(context) == WindowSize.TABLET) 150.dp else 200.dp
    val infoImage = painterResource(id = R.drawable.search_info)
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = infoImage,
            contentDescription = "선수 이름, 응원가 제목으로 검색할 수 있어요.",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(imageSize)
        )
    }
}

@Composable
fun EmptyView() {
    val context = LocalContext.current
    val emptyImage = painterResource(id = R.drawable.search_empty)
    val imageSize = if (Utility.getDeviceType(context) == WindowSize.TABLET) 100.dp else 200.dp
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = emptyImage,
            contentDescription = "검색 결과가 없어요.",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(imageSize)
        )
        RequestMusicButton(color = MoyaColor.mainGreen)
    }
}
@Composable
fun SearchBar(viewModel: SearchViewModel) {

    val text by viewModel.searchText.collectAsState()

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester()}

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column (
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .background(MoyaColor.background)
            .height(117.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            BasicTextField(
                value = text,
                onValueChange = { viewModel.setSearchText(it) },
                textStyle = getTextStyle(style = MoyaFont.CustomBodyBold),
                modifier = Modifier
                    .background(
                        color = MoyaColor.gray,
                        shape = RoundedCornerShape(32.5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .focusRequester(focusRequester)
                    .weight(1f),
                interactionSource = interactionSource,
                maxLines = 1,
                decorationBox = { innerTextField ->

                    Box {
                        if (text.isEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "",
                                    tint = MoyaColor.darkGray
                                )
                                Text(
                                    text = "검색",
                                    style = getTextStyle(style = MoyaFont.CustomBodyBold),
                                    color = MoyaColor.darkGray
                                )
                            }

                        } else {
                            IconButton(
                                onClick = { viewModel.setSearchText("") },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear text",
                                    modifier = Modifier
                                        .background(
                                            color = MoyaColor.darkGray,
                                            shape = CircleShape
                                        )
                                        .padding(4.dp),
                                    tint = MoyaColor.gray
                                )
                            }
                        }
                        innerTextField()
                    }
                }
            )
            if (isFocused) {
                Text(
                    text = "취소",
                    color = MoyaColor.darkGray,
                    style = getTextStyle(style = MoyaFont.CustomBodyMedium),
                    modifier = Modifier
                        .clickable {
                            focusManager.clearFocus()
                        }
                        .padding(start = 12.dp)
                )
            }
        }


        Divider(
            color = MoyaColor.gray,
            modifier = Modifier.padding(top = 20.dp)
        )
    }

}

@Composable
fun listItem(music: MusicInfo, onClickEvent: () -> Unit) {
    Row(
        modifier = Modifier
            .background(MoyaColor.white)
            .clickable(onClick = onClickEvent),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    painter = painterResource(id = music.team.getTeamAlbumImageResourceId()),
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(
                        text = music.title,
                        color = MoyaColor.black,
                        style = getTextStyle(style = MoyaFont.CustomBodyMedium)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = music.team.getKrTeamName(),
                        color = MoyaColor.darkGray,
                        style = getTextStyle(style = MoyaFont.CustomCaptionMedium)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "arrow right",
                    tint = MoyaColor.darkGray,
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    val viewModel = SearchViewModel(application = Application())
    val navController = rememberNavController()

    SearchScreen(
        viewModel = viewModel,
        navController = navController
    )
}
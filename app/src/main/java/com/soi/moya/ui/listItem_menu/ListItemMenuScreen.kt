package com.soi.moya.ui.listItem_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soi.moya.R
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun ListItemMenuScreen(
    music: MusicInfo,
    team: Team,
    onClick: ()->Unit
) {
    val viewModel: ListItemMenuViewModel = hiltViewModel()

    val doesExist = remember { mutableStateOf(false) }
    LaunchedEffect(music.id) {
        doesExist.value = viewModel.doesItemExist(music.id)
    }
    Column(
        modifier = Modifier.background(color = MoyaColor.background)
    ) {
        MenuHeader(music = music, team = team)
        Divider(
            color = MoyaColor.gray,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 10.dp))
        MenuItem(
            doesExist.value,
            favorite = {
                viewModel.saveItem(music = music, team = team)
                onClick()
            },
            favoriteCancle = {
                viewModel.deleteItem(music = music)
                onClick()
            })
    }
}

@Composable
fun MenuItem(doesItemExist: Boolean, favorite: () -> Unit, favoriteCancle: () -> Unit) {
    val image = if (doesItemExist) R.drawable.favorite_cancle else R.drawable.favorite
    val text = if (doesItemExist) R.string.favorite_cancle else R.string.favorite
    Row (
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .clickable {
                       if (doesItemExist) {
                           favoriteCancle()
                       } else {
                           favorite()
                       }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = image),
            contentDescription = "menu icon_favorite",
            contentScale = ContentScale.Inside
        )

        Text(
            text = stringResource(id = text),
            style = getTextStyle(style = MoyaFont.CustomBodyMedium),
            color = MoyaColor.black)
    }
}

@Composable
fun MenuHeader(music: MusicInfo, team: Team) {
    val image = if (music.type) team.getPlayerAlbumImageResourceId() else team.getTeamAlbumImageResourceId()
    Row (
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Image(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .shadow(
                    4.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = true,
                ),
            painter = painterResource(id = image),
            contentDescription = "music album image")
        Column (
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = music.title,
                style = getTextStyle(style = MoyaFont.CustomTitleBold),
                color = MoyaColor.black
            )
            Text(
                text = team.getKrTeamName(),
                style = getTextStyle(style = MoyaFont.CustomCaptionMedium),
                color = MoyaColor.darkGray
            )
        }
    }
}

@Preview
@Composable
fun MainMenuScreenPreview() {
    val music = MusicInfo(id="", info="", lyrics = "", title = "정훈", type = true, url="")
    val team = Team.doosan
    ListItemMenuScreen(music, team, onClick = {})
}
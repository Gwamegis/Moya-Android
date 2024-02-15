package com.soi.moya.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.models.Music
import com.soi.moya.models.Team
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun MusicListItem(
    music: Music,
    team: Team,
    buttonImageResourceId: Int,
    onClickCell: (music: Music) -> Unit,
    onClickExtraButton: (music: Music) -> Unit
) {
    Row(
        modifier = Modifier
            .background(MoyaColor.white)
            .clickable {
                onClickCell(music)
            },
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MusicInfoView(music = music, team = team)
            }
        }

        MusicListExtraButton(modifier = Modifier.align(Alignment.CenterVertically),
            resourceId = buttonImageResourceId,
            onClick = { onClickExtraButton(music) })
    }
}

@Composable
fun MusicInfoView(music: Music, team: Team) {
    Image(
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
        // TODO: Music 객체로 팀 확인하는 로직 생각해보기
        painter = painterResource(id = if (music.type) team.getPlayerAlbumImageResourceId() else team.getTeamAlbumImageResourceId()),
        contentDescription = null,
    )
    Column(
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Text(
            music.title, color = MoyaColor.black,
            style = getTextStyle(style = MoyaFont.CustomBodyMedium)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            music.info, color = MoyaColor.darkGray,
            style = getTextStyle(style = MoyaFont.CustomCaptionMedium)
        )
    }
}

@Composable
fun MusicListExtraButton(modifier: Modifier, resourceId: Int, onClick: () -> Unit) {
    Icon(
        modifier = modifier
            .padding(end = 20.dp)
            .size(16.dp)
            .clickable {
                onClick()
            },
        painter = painterResource(id = resourceId),
        contentDescription = null,
        tint = MoyaColor.darkGray
    )
}

@Preview
@Composable
fun MusicListItemPreview() {
    MusicListItem(
        music = Music(title = "Title", info = "SubTitle"),
        team = Team.doosan,
        onClickCell = {},
        onClickExtraButton = {},
        buttonImageResourceId = R.drawable.ellipse
    )
}
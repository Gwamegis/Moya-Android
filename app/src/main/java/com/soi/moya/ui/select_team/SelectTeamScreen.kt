package com.soi.moya.ui.select_team

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.soi.moya.Component.ButtonContainer
import com.soi.moya.R
import com.soi.moya.models.Team
import com.soi.moya.models.getTeamImage
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

class SelectTeamScreen: ComponentActivity() {
    private val viewModel: SelectTeamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    contentPadding = PaddingValues(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item (
                        span = {
                            GridItemSpan(maxLineSpan)
                        }) {
                        Text(
                            text = "어느 팀을 응원하시나요?",
                            style = getTextStyle(style = MoyaFont.CustomTitleBold),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    items(Team.values().size) {index ->
                        ImageItem(
                            team = Team.values()[index],
                            viewModel = viewModel
                        )
                    }
                    item (span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        ButtonContainer(
                            text = "응원하러 가기",
                            textColor = Color.White,
                            bgColor = MoyaColor().mainGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 30.dp),
                            isEnabled = { viewModel.selectedTeam.value != null },
                            onClick = { viewModel.onClickNext() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageItem(team: Team, viewModel: SelectTeamViewModel) {
    val image = getTeamImage(team = team)
    val checkedImage = painterResource(id = R.drawable.select_team)

    Button(
        onClick = { viewModel.onTeamSelected(team) },
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
    ) {
        Box {
            Image(painter = image, contentDescription = null)
            if (viewModel.selectedTeam.value == team) {
                Image(painter = checkedImage, contentDescription = null)
            }
        }
    }
}
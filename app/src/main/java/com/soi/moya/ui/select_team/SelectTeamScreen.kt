package com.soi.moya.ui.select_team

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soi.moya.ui.component.ButtonContainer
import com.soi.moya.R
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch

@Composable
fun SelectTeamScreen(viewModel: SelectTeamViewModel) {

    Surface(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item (
                span = {
                    GridItemSpan(maxLineSpan)
                }) {
                Text(
                    text = stringResource(R.string.select_team_title),
                    style = getTextStyle(style = MoyaFont.CustomTitleBold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            items(viewModel.teams.size) {index ->
                ImageItem(
                    team = viewModel.teams[index],
                    viewModel = viewModel
                )
            }
            item (span = {
                GridItemSpan(maxLineSpan)
            }) {
                ButtonContainer(
                    text = stringResource(R.string.select_team_button),
                    textColor = Color.White,
                    bgColor = MoyaColor().mainGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 30.dp),
                    isEnabled = { viewModel.selectedTeam.value != null },
                    onClick = {
                        viewModel.onClickNext()
                    }
                )
            }
        }
    }
}

@Composable
fun ImageItem(team: Team, viewModel: SelectTeamViewModel) {
    val image = painterResource(id = team.getTeamImageResourceId())
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
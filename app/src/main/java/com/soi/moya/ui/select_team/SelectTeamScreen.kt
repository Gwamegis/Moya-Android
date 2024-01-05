package com.soi.moya.ui.select_team

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

class SelectTeamScreen: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ){
                    Text(text = "어느 팀을 응원하시나요?", style = getTextStyle(style = MoyaFont.CustomTitleBold))
                    TeamSelectList()
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ButtonContainer(color = Color.Red, text = "응원하러가기", modifier = Modifier.weight(1f))
                        ButtonContainer(color = MoyaColor().mainGreen, text = "응원하러가기", modifier = Modifier.weight(1f))
                    }
                    ButtonContainer(color = MoyaColor().mainGreen, text = "응원하러가기", modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun TeamSelectList() {
    val teams = Team.values()
    var selectedTeam by remember {
        mutableStateOf<Team?>(null)
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(teams.size) {team ->
            ImageItem(
                team = Team.values()[team],
                selectedTeam = selectedTeam,
                onTeamSelected = {
                    if(selectedTeam == it) {
                        selectedTeam = null
                    } else {
                        selectedTeam = it
                    }
                }
            )
        }
    }
}
@Composable
fun ImageItem(team: Team, selectedTeam: Team?, onTeamSelected: (Team) -> Unit) {
    val image = getTeamImage(team = team)
    val checkedImage = painterResource(id = R.drawable.select_team)

    Button(
        onClick = { onTeamSelected(team) },
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        enabled = selectedTeam != null
    ) {
        Box {
            Image(painter = image, contentDescription = null)
            if (selectedTeam == team) {
                Image(painter = checkedImage, contentDescription = null)
            }
        }
    }
}

@Composable
fun ButtonContainer(color: Color, text: String, font: MoyaFont = MoyaFont.CustomBodyBold, modifier: Modifier = Modifier) {
     Button(
         onClick = { println("버튼클릭") },
         colors = ButtonDefaults.buttonColors(backgroundColor = color),
         contentPadding = PaddingValues(20.dp),
         shape = RoundedCornerShape(8.dp),
         modifier = modifier
     ) {
         Text(
             text = text,
             color = Color.White,
             style = getTextStyle(style = font),
         )
     }
}

enum class Team(
    val KrName: String,
    val EngName: String
) {
    Doosan("두산 베어스", "Doosan"),
    Hanwha("한화 이글스", "Hanwha"),
    Samsung("삼성 라이온즈", "Samsung"),
    Lotte("롯데 자이언츠", "Lotte"),
    Lg("엘지 트윈스", "LG"),
    Ssg("쓱 렌더스", "SSG"),
    Kt("케이티 위즈", "KT"),
    Nc("엔씨 다이노스", "NC"),
    Kiwoom("키움 히어로즈", "Kiwoom"),
    Kia("키아 타이거즈", "Kia")
}
@Composable
private fun getTeamImage(team: Team): Painter {
    return when(team) {
        Team.Doosan -> { painterResource(id = R.drawable.select_team_doosan)}
        Team.Hanwha -> { painterResource(id = R.drawable.select_team_hanwha)}
        Team.Samsung -> { painterResource(id = R.drawable.select_team_samsung)}
        Team.Lotte -> { painterResource(id = R.drawable.select_team_lotte)}
        Team.Lg -> { painterResource(id = R.drawable.select_team_lg)}
        Team.Ssg -> { painterResource(id = R.drawable.select_team_ssg)}
        Team.Kt -> { painterResource(id = R.drawable.select_team_kt)}
        Team.Nc -> { painterResource(id = R.drawable.select_team_nc)}
        Team.Kiwoom -> { painterResource(id = R.drawable.select_team_kiwoom)}
        Team.Kia -> { painterResource(id = R.drawable.select_team_kia)}
    }
}


@Preview(showBackground = true)
@Composable
fun SelectTeamScreenPreview() {
    Surface(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Text(text = "어느 팀을 응원하시나요?", style = getTextStyle(style = MoyaFont.CustomTitleBold))
            TeamSelectList()
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonContainer(color = Color.Red, text = "응원하러가기", modifier = Modifier.weight(1f))
                ButtonContainer(color = MoyaColor().mainGreen, text = "응원하러가기", modifier = Modifier.weight(1f))
            }
            ButtonContainer(color = MoyaColor().mainGreen, text = "응원하러가기", modifier = Modifier.fillMaxWidth())
        }
    }
}


package com.soi.moya.models
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.soi.moya.R
import com.soi.moya.ui.theme.MoyaColor

enum class Team {
    doosan, hanwha, samsung, lotte, lg, ssg, ktWiz, nc, kiwoom, kia;

    fun getBackgroundColor(color: MoyaColor = MoyaColor()): androidx.compose.ui.graphics.Color {
        return when (this) {
            doosan -> color.doosanBackground
            hanwha -> color.hanwhaBackground
            samsung -> color.samsungBackground
            lotte -> color.lotteBackground
            lg -> color.lgBackground
            ssg -> color.ssgBackground
            ktWiz -> color.ssgBackground
            nc -> color.ncBackground
            kiwoom -> color.kiwoomBackground
            kia -> color.kiaBackground
        }
    }

    fun getPointColor(color: MoyaColor = MoyaColor()): androidx.compose.ui.graphics.Color {
        return when (this) {
            doosan -> color.doosanPoint
            hanwha -> color.hanwhaPoint
            samsung -> color.samsungPoint
            lotte -> color.lottePoint
            lg -> color.lgPoint
            ssg -> color.ssgPoint
            ktWiz -> color.ssgPoint
            nc -> color.ncPoint
            kiwoom -> color.kiwoomPoint
            kia -> color.kiaPoint
        }
    }

    fun getSubColor(color: MoyaColor = MoyaColor()): androidx.compose.ui.graphics.Color {
        return when (this) {
            doosan -> color.doosanSub
            hanwha -> color.hanwhaSub
            samsung -> color.samsungSub
            lotte -> color.lotteSub
            lg -> color.lgSub
            ssg -> color.ssgSub
            ktWiz -> color.ssgSub
            nc -> color.ncSub
            kiwoom -> color.kiwoomSub
            kia -> color.kiaSub
        }
    }
}

@Composable
fun getTeamImage(team: Team): Painter {
    return when(team) {
        Team.doosan -> { painterResource(id = R.drawable.select_team_doosan) }
        Team.hanwha -> { painterResource(id = R.drawable.select_team_hanwha) }
        Team.samsung -> { painterResource(id = R.drawable.select_team_samsung) }
        Team.lotte -> { painterResource(id = R.drawable.select_team_lotte) }
        Team.lg -> { painterResource(id = R.drawable.select_team_lg) }
        Team.ssg -> { painterResource(id = R.drawable.select_team_ssg) }
        Team.ktWiz -> { painterResource(id = R.drawable.select_team_kt)  }
        Team.nc -> { painterResource(id = R.drawable.select_team_nc) }
        Team.kiwoom -> { painterResource(id = R.drawable.select_team_kiwoom) }
        Team.kia -> { painterResource(id = R.drawable.select_team_kia) }
    }
}
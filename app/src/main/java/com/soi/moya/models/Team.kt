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
    fun getTeamImageResourceId(): Int {
        return when (this) {
            doosan -> R.drawable.select_team_doosan
            hanwha -> R.drawable.select_team_hanwha
            samsung -> R.drawable.select_team_samsung
            lotte -> R.drawable.select_team_lotte
            lg -> R.drawable.select_team_lg
            ssg -> R.drawable.select_team_ssg
            ktWiz -> R.drawable.select_team_kt
            nc -> R.drawable.select_team_nc
            kiwoom -> R.drawable.select_team_kiwoom
            kia -> R.drawable.select_team_kia
        }
    }
}
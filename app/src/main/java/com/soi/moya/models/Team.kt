package com.soi.moya.models
import com.soi.moya.R
import com.soi.moya.ui.theme.MoyaColor

enum class Team {
    doosan, hanwha, samsung, lotte, lg, ssg, ktWiz, nc, kiwoom, kia;

    fun getBackgroundColor(): androidx.compose.ui.graphics.Color {
        return when (this) {
            doosan -> MoyaColor.doosanBackground
            hanwha -> MoyaColor.hanwhaBackground
            samsung -> MoyaColor.samsungBackground
            lotte -> MoyaColor.lotteBackground
            lg -> MoyaColor.lgBackground
            ssg -> MoyaColor.ssgBackground
            ktWiz -> MoyaColor.ktBackground
            nc -> MoyaColor.ncBackground
            kiwoom -> MoyaColor.kiwoomBackground
            kia -> MoyaColor.kiaBackground
        }
    }

    fun getPointColor(): androidx.compose.ui.graphics.Color {
        return when (this) {
            doosan -> MoyaColor.doosanPoint
            hanwha -> MoyaColor.hanwhaPoint
            samsung -> MoyaColor.samsungPoint
            lotte -> MoyaColor.lottePoint
            lg -> MoyaColor.lgPoint
            ssg -> MoyaColor.ssgPoint
            ktWiz -> MoyaColor.ktPoint
            nc -> MoyaColor.ncPoint
            kiwoom -> MoyaColor.kiwoomPoint
            kia -> MoyaColor.kiaPoint
        }
    }

    fun getSubColor(): androidx.compose.ui.graphics.Color {
        return when (this) {
            doosan -> MoyaColor.doosanSub
            hanwha -> MoyaColor.hanwhaSub
            samsung -> MoyaColor.samsungSub
            lotte -> MoyaColor.lotteSub
            lg -> MoyaColor.lgSub
            ssg -> MoyaColor.ssgSub
            ktWiz -> MoyaColor.ktSub
            nc -> MoyaColor.ncSub
            kiwoom -> MoyaColor.kiwoomSub
            kia -> MoyaColor.kiaSub
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
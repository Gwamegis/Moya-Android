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

    fun getTeamAlbumImageResourceId(): Int {
        return when (this) {
            doosan -> R.drawable.album_doosan
            hanwha -> R.drawable.album_hanwha
            samsung -> R.drawable.album_samsung
            lotte -> R.drawable.album_lotte
            lg -> R.drawable.album_lg
            ssg -> R.drawable.album_ssg
            ktWiz -> R.drawable.album_kt
            nc -> R.drawable.album_nc
            kiwoom -> R.drawable.album_kiwoom
            kia -> R.drawable.album_kia
        }
    }
    fun getPlayerAlbumImageResourceId(): Int {
        return when (this) {
            doosan -> R.drawable.player_doosan
            hanwha -> R.drawable.player_hanwha
            samsung -> R.drawable.player_samsung
            lotte -> R.drawable.player_lotte
            lg -> R.drawable.player_lg
            ssg -> R.drawable.player_ssg
            ktWiz -> R.drawable.player_kt
            nc -> R.drawable.player_nc
            kiwoom -> R.drawable.player_kiwoom
            kia -> R.drawable.player_kia
        }
    }

    fun getSeasonSongAlbumImageResourceId(): Int {
        return when (this) {
            doosan -> R.drawable.doosan24
            hanwha -> R.drawable.hanwha24
            samsung -> R.drawable.samsung24
            lotte -> R.drawable.lotte24
            lg -> R.drawable.lg24
            ssg -> R.drawable.ssg24
            ktWiz -> R.drawable.kt24
            nc -> R.drawable.nc24
            kiwoom -> R.drawable.kiwoom24
            kia -> R.drawable.kia24
        }
    }

    fun getMainBannerImageResourceId(): Int {
        return when (this) {
            doosan -> R.drawable.main_banner_doosan
            hanwha -> R.drawable.main_banner_hanwha
            samsung -> R.drawable.main_banner_samsung
            lotte -> R.drawable.main_banner_lotte
            lg -> R.drawable.main_banner_lg
            ssg -> R.drawable.main_banner_ssg
            ktWiz -> R.drawable.main_banner_kt
            nc -> R.drawable.main_banner_nc
            kiwoom -> R.drawable.main_banner_kiwoom
            kia -> R.drawable.main_banner_kia
        }
    }
    fun getKrTeamName(): String {
        return when (this) {
            doosan -> "두산 베어스"
            hanwha -> "한화 이글스"
            samsung -> "삼성 라이온즈"
            lotte -> "롯데 자이언츠"
            lg -> "엘지 트윈스"
            ssg -> "쓱 랜더스"
            ktWiz -> "케이티 위즈"
            nc -> "엔씨 다이노스"
            kiwoom -> "키움 히어로즈"
            kia -> "기아 타이거즈"
        }
    }

    fun getFirebaseCollectionName(): String {
        return when (this) {
            doosan -> "Doosan"
            hanwha -> "Hanwha"
            samsung -> "Samsung"
            lotte -> "Lotte"
            lg -> "LG"
            ssg -> "SSG"
            ktWiz -> "Kt"
            nc -> "NC"
            kiwoom -> "Kiwoom"
            kia -> "Kia"
        }
    }
}
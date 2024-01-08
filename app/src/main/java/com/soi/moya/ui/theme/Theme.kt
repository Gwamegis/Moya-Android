package com.soi.moya.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.soi.moya.models.Team

@Composable
fun MoyaTheme(
    team: Team = Team.hanwha,
    content: @Composable () -> Unit
) {
    val rememberedColors = remember { getColorScheme(team).copy() }.apply { updateColorsFrom(getColorScheme(team).copy()) }
    CompositionLocalProvider(
        LocalColors provides rememberedColors) {
        MaterialTheme(
            content = content
        )
    }
}

fun initColors() = MoyaTeamColors(
    background = MoyaColor().doosanBackground,
    point = MoyaColor().doosanPoint,
    sub =  MoyaColor().doosanSub
)

private fun getColorScheme(team: Team): MoyaTeamColors {
    return when (team) {
        Team.doosan -> doosanColorScheme
        Team.hanwha -> hanwhaColorScheme
        Team.samsung -> samsungColorScheme
        Team.lotte -> lotteColorScheme
        Team.lg -> lgColorScheme
        Team.ssg -> ssgColorScheme
        Team.ktWiz -> ktColorScheme
        Team.nc -> ncColorScheme
        Team.kiwoom -> kiwoomColorScheme
        Team.kia -> kiaColorScheme
    }
}

private val doosanColorScheme = MoyaTeamColors(
    background = MoyaColor().doosanBackground,
    point = MoyaColor().doosanPoint,
    sub =  MoyaColor().doosanSub
)

private val hanwhaColorScheme = MoyaTeamColors(
    background = MoyaColor().hanwhaBackground,
    point = MoyaColor().hanwhaPoint,
    sub =  MoyaColor().hanwhaSub
)

private val samsungColorScheme = MoyaTeamColors(
    background = MoyaColor().samsungBackground,
    point = MoyaColor().samsungPoint,
    sub =  MoyaColor().samsungSub
)

private val lotteColorScheme = MoyaTeamColors(
    background = MoyaColor().lotteBackground,
    point = MoyaColor().lottePoint,
    sub =  MoyaColor().lotteSub
)

private val lgColorScheme = MoyaTeamColors(
    background = MoyaColor().lgBackground,
    point = MoyaColor().lgPoint,
    sub =  MoyaColor().lgSub
)

private val ssgColorScheme = MoyaTeamColors(
    background = MoyaColor().ssgBackground,
    point = MoyaColor().ssgPoint,
    sub =  MoyaColor().ssgSub
)

private val ktColorScheme = MoyaTeamColors(
    background = MoyaColor().ktBackground,
    point = MoyaColor().ktPoint,
    sub =  MoyaColor().ktSub
)

private val ncColorScheme = MoyaTeamColors(
    background = MoyaColor().ncBackground,
    point = MoyaColor().ncPoint,
    sub =  MoyaColor().ncSub
)

private val kiwoomColorScheme = MoyaTeamColors(
    background = MoyaColor().kiwoomBackground,
    point = MoyaColor().kiwoomPoint,
    sub =  MoyaColor().kiwoomSub
)

private val kiaColorScheme = MoyaTeamColors(
    background = MoyaColor().kiaBackground,
    point = MoyaColor().kiaPoint,
    sub =  MoyaColor().kiaSub
)
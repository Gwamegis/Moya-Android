package com.soi.moya.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.soi.moya.R

private val pretendard = FontFamily(
    Font(R.font.pretendard_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.pretendard_bold, FontWeight.Bold, FontStyle.Normal),
)

private val baseTextStyle = TextStyle(
    fontStyle = FontStyle.Normal,
    fontFamily = pretendard,
)
enum class MoyaFont {
    CustomTitleBold,
    CustomTitleMedium,
    CustomBodyBold,
    CustomBodyMedium,
    CustomCaptionBold,
    CustomCaptionMedium,
    CustomHeadline,
    CustomHeadlineBold,
    CustomStorageHeaderTitle,
}

@Composable
fun getTextStyle(style: MoyaFont): TextStyle {
    return when(style) {
        MoyaFont.CustomTitleBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(20.dp),
                lineHeight = dpToSp(20.dp)
            )
        }
        MoyaFont.CustomTitleMedium -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(20.dp),
                lineHeight = dpToSp(20.dp)
            )
        }
        MoyaFont.CustomBodyBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(16.dp),
                lineHeight = dpToSp(16.dp)
            )
        }
        MoyaFont.CustomBodyMedium -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(16.dp),
                lineHeight = dpToSp(21.dp)
            )
        }
        MoyaFont.CustomCaptionBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(12.dp),
                lineHeight = dpToSp(12.dp)
            )
        }
        MoyaFont.CustomCaptionMedium -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(10.dp),
                lineHeight = dpToSp(10.dp)
            )
        }
        MoyaFont.CustomHeadline -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(26.dp),
                lineHeight = dpToSp(50.dp)
            )
        }
        MoyaFont.CustomHeadlineBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(26.dp),
                lineHeight = dpToSp(45.dp)
            )
        }

        MoyaFont.CustomStorageHeaderTitle -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(40.dp),
                lineHeight = dpToSp(40.dp)
            )
        }
    }
}

@Composable
private fun dpToSp(dp: Dp) = with(LocalDensity.current) {
    dp.toSp()
}
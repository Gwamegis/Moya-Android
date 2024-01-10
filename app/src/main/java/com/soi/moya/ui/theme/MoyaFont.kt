package com.soi.moya.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
    CustomStorageHeaderTitle,
}

@Composable
fun getTextStyle(style: MoyaFont): TextStyle {
    return when(style) {
        MoyaFont.CustomTitleBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 20.sp
            )
        }
        MoyaFont.CustomTitleMedium -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 20.sp
            )
        }
        MoyaFont.CustomBodyBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
        }
        MoyaFont.CustomBodyMedium -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
        }
        MoyaFont.CustomCaptionBold -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 12.sp
            )
        }
        MoyaFont.CustomCaptionMedium -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 10.sp
            )
        }
        MoyaFont.CustomHeadline -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                lineHeight = 50.sp
            )
        }
        MoyaFont.CustomStorageHeaderTitle -> {
            baseTextStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 40.sp
            )
        }
    }
}
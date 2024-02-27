package com.soi.moya.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class WindowSize {
    NORMAL, MINI, TABLET
}
object Utility {
    fun getDeviceType(context: Context): WindowSize {
        val display = context.resources.displayMetrics

        val deviceType =
            if(display == null) {
                WindowSize.NORMAL
            }else {
                // 태블릿, 폴드 펼침
                if(display.widthPixels > 1600) {
                    WindowSize.TABLET
                }
                // 미니, 폴드 닫힘
                else if(display.widthPixels < 980) {
                    WindowSize.MINI
                }
                // 일반
                else{
                    WindowSize.NORMAL
                }
            }

        return deviceType
    }
    fun getCurrentTimeString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    @Composable
    fun dpToSp(dp: Dp) = with(LocalDensity.current) {
        dp.toSp()
    }
}
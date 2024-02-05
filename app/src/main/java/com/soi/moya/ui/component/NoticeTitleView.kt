package com.soi.moya.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

interface NoticeType {
    fun getTitle(): String
    fun getImageResourceID(): Int
}

enum class Notice : NoticeType {
    NEW_FEATURES {
        override fun getTitle() = "새로운 기능이 생겼어요"
        override fun getImageResourceID() = R.drawable.alarm
    },

    SERVER_CHECK {
        override fun getTitle() = "서버 점검 공지"
        override fun getImageResourceID() = R.drawable.warning
    }
}

@Composable
fun NoticeTitleView(type: NoticeType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    ) {
        Image(
            painter = painterResource(id = type.getImageResourceID()),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 20.dp)
        )

        Text(
            text = type.getTitle(),
            style = getTextStyle(style = MoyaFont.CustomTitleBold)
        )
    }
}
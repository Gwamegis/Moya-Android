package com.soi.moya.ui.notice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soi.moya.ui.component.ButtonContainer
import com.soi.moya.ui.component.Notice
import com.soi.moya.ui.component.NoticeTitleView
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun ServerTrafficNoticeScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
    ) {
        NoticeTitleView(type = Notice.SERVER_CHECK)
        TrafficDescriptionView(description = "우리는 파이어베이스를 쓰고 있습니다. 왜 느릴까요? 왜 데이터가 호출이 안 되죠? ㅠ 저도 너무 눈물 나요. . 일시적인 문제일겁니다. . . . ")
        ButtonView()
    }
}

@Composable
fun TrafficDescriptionView(description: String) {
    Text(
        text = description,
        style = getTextStyle(style = MoyaFont.CustomBodyMedium),
        color = MoyaColor.darkGray,
        modifier = Modifier
            .padding(bottom = 30.dp)
    )
}

@Composable
fun ButtonView() {
    ButtonContainer(
        text = "확인",
        bgColor = MoyaColor.mainGreen,
        textColor = MoyaColor.white,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        onClick = { }
    )
}

@Composable
@Preview
fun ServerTrafficNoticeScreenPreview() {
    ServerTrafficNoticeScreen()
}
package com.soi.moya.ui.notice.traffic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soi.moya.models.Traffic
import com.soi.moya.ui.component.ButtonContainer
import com.soi.moya.ui.component.Notice
import com.soi.moya.ui.component.NoticeTitleView
import com.soi.moya.ui.notice.TrafficDescriptionView
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun TrafficNoticeScreen(
    traffic: Traffic,
    onDismissRequest: () -> Unit
) {
    TrafficNoticeView(
        traffic = traffic
    )
}

@Composable
private fun TrafficNoticeView(
    traffic: Traffic
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
    ) {
        NoticeTitleView(type = Notice.SERVER_CHECK)
        TrafficDescriptionView(description = traffic.description)
        com.soi.moya.ui.notice.ButtonView()
    }
}

@Composable
private fun DescriptionView(
    description: String,
) {
    Text(
        text = description,
        style = getTextStyle(style = MoyaFont.CustomBodyMedium),
        color = MoyaColor.darkGray,
        modifier = Modifier
            .padding(bottom = 30.dp)
    )
}

@Composable
private fun ButtonView() {
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
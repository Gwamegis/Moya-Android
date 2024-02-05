package com.soi.moya.ui.notice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.ui.component.ButtonContainer
import com.soi.moya.ui.component.Notice
import com.soi.moya.ui.component.NoticeTitleView
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun NewFeatureNoticeScreen() {
    val descriptions = arrayOf(
        "첫 번째 새로운 기능은 뷰를 만들었어요!",
        "두 번째 새로운 기능은 좀 길어요. 제 말을 들어보시겠어요? 저는 너무 피곤하고 너무 힘들어요",
        "세 번째 기능은 예 이겁니다."
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
    ) {
        NoticeTitleView(type = Notice.NEW_FEATURES)
        FeatureDescriptionsView(descriptions = descriptions)
        ButtonsView()
    }
}

@Composable
fun FeatureDescriptionsView(descriptions: Array<String>) {
    Column(
        modifier = Modifier
            .padding(bottom = 32.dp)
    ) {
        LazyColumn {
            itemsIndexed(descriptions) { index, description ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.new_feature_rectangle),
                            contentDescription = null
                        )
                        Text(
                            text = "${index + 1}",
                            style = getTextStyle(style = MoyaFont.CustomBodyBold),
                            modifier = Modifier
                                .align(Alignment.Center),
                            color = MoyaColor.white
                        )
                    }

                    Text(
                        text = description,
                        style = getTextStyle(MoyaFont.CustomBodyMedium),
                        color = MoyaColor.darkGray,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

            }
        }
    }
}

@Composable
fun ButtonsView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        ButtonContainer(
            text = "다음에 할래요",
            bgColor = MoyaColor.gray,
            textColor = MoyaColor.darkGray,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            onClick = { }
        )

        ButtonContainer(
            text = "업데이트하러 가기",
            bgColor = MoyaColor.mainGreen,
            textColor = MoyaColor.white,
            modifier = Modifier
                .weight(1f),
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetWithMultipleDescriptionsPreview() {
    NewFeatureNoticeScreen()
}
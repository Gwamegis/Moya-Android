package com.soi.moya.ui.notice.traffic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.models.Traffic
import com.soi.moya.ui.component.ButtonContainer
import com.soi.moya.ui.component.Notice
import com.soi.moya.ui.component.NoticeTitleView
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrafficNoticeScreen(
    sheetState: ModalBottomSheetState,
    traffic: Traffic?,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            traffic?.let {
                TrafficNoticeView(
                    traffic = traffic,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                        }
                    }
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun TrafficNoticeView(
    traffic: Traffic,
    onDismissRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
    ) {
        NoticeTitleView(type = Notice.SERVER_CHECK)
        DescriptionView(description = traffic.description)
        ButtonView(
            onDismissRequest = onDismissRequest
        )
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
private fun ButtonView(
    onDismissRequest: () -> Unit
) {
    ButtonContainer(
        text = stringResource(id = R.string.confirm),
        bgColor = MoyaColor.mainGreen,
        textColor = MoyaColor.white,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        onClick =
            onDismissRequest
    )
}
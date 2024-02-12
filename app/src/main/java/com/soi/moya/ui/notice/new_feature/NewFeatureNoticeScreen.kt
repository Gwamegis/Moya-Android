package com.soi.moya.ui.notice.new_feature

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.models.Version
import com.soi.moya.ui.component.ButtonContainer
import com.soi.moya.ui.component.Notice
import com.soi.moya.ui.component.NoticeTitleView
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewFeatureNoticeScreen(
    sheetState: ModalBottomSheetState,
    version: Version?,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            version?.let {
                NewFeatureNoticeView(
                    version = version,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                        }
                        onDismissRequest()
                    }
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun NewFeatureNoticeView(
    version: Version,
    onDismissRequest: () -> Unit,
) {
    // todo: context 삭제
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(MoyaColor.white)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            NoticeTitleView(type = Notice.NEW_FEATURES)
            DescriptionView(descriptions = version.feature)
            ButtonView(
                onDismissRequest = onDismissRequest,
                onUpdateRequest = { openPlayStore(context = context) }
            )
        }
    }
}

@Composable
private fun DescriptionView(
    descriptions: List<String>,
) {
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
private fun ButtonView(
    onDismissRequest: () -> Unit,
    onUpdateRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        ButtonContainer(
            text = stringResource(id = R.string.download_next_launch),
            bgColor = MoyaColor.gray,
            textColor = MoyaColor.darkGray,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            onClick = {
                onDismissRequest()
            }
        )

        ButtonContainer(
            text = stringResource(id = R.string.go_to_play_store),
            bgColor = MoyaColor.mainGreen,
            textColor = MoyaColor.white,
            modifier = Modifier
                .weight(1f),
            onClick = {
                onUpdateRequest()
                onDismissRequest()
            }
        )
    }
}

private fun openPlayStore(context: Context) {
    val appPackageName = context.packageName
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$appPackageName")
    )
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
    )

    try {
        context.startActivity(marketIntent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(webIntent)
    }
}
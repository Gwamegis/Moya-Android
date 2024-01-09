package com.soi.moya.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soi.moya.R
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun RequestMusicButton(color: Color) {

    val context = LocalContext.current
    val webpage: Uri = Uri.parse("https://forms.gle/522hhU1Riq5wQhbv7")
    val intent = Intent(Intent.ACTION_VIEW, webpage)

    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {

        OutlinedButton(
            onClick = { context.startActivity(intent) },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = color
            ),
            border = BorderStroke(1.dp, color),
            shape = RoundedCornerShape(30.dp),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 8.dp)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
            Text(text = stringResource(id = R.string.request_music),
                style = getTextStyle(style = MoyaFont.CustomBodyBold)
            )
        }
    }
}
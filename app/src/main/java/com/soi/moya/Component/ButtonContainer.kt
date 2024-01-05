package com.soi.moya.Component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

@Composable
fun ButtonContainer(
    text: String,
    bgColor: Color,
    textColor: Color,
    font: MoyaFont = MoyaFont.CustomBodyBold,
    modifier: Modifier = Modifier,
    isEnabled: () -> Boolean = { true },
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = bgColor),
        contentPadding = PaddingValues(20.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        enabled = isEnabled()
    ) {
        Text(
            text = text,
            color = textColor,
            style = getTextStyle(style = font),
        )
    }
}
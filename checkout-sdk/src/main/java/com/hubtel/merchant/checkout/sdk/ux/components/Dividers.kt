package com.hubtel.merchant.checkout.sdk.ux.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

@Composable
fun HBDivider(
    modifier: Modifier = Modifier,
    color: Color = HubtelTheme.colors.outline,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    Divider(
        modifier,
        color = color,
        thickness = thickness,
        startIndent = startIndent,
    )
}

@Composable
fun HBDashedDivider(
    modifier: Modifier = Modifier,
    color: Color = HubtelTheme.colors.outline,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    dashLength: Dp = 5.dp,
    spacing: Dp = 5.dp
) {

    val indentMod = if (startIndent.value != 0f) {
        Modifier.padding(start = startIndent)
    } else {
        Modifier
    }

    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }


    val density = LocalDensity.current
    val dashLengthPx = with(density) { dashLength.toPx() }
    val spacingPx = with(density) { spacing.toPx() }

    val targetThicknessPx = with(density) { targetThickness.toPx() }

    val lineBrush = remember(color) { SolidColor(color) }

    Canvas(
        modifier
            .then(indentMod)
            .fillMaxWidth()
            .height(targetThickness)
    ) {
        val offsetLineY = this.size.height * 0.5f

        drawLine(
            lineBrush,
            Offset(0f, offsetLineY),
            Offset(size.width, offsetLineY),
            targetThicknessPx,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLengthPx, spacingPx),
            )
        )
    }
}

/*
@Preview
@Composable
internal fun DashPreview() {
    HubtelTheme {
        Surface(
            Modifier.size(500.dp)
        ) {
            HBDashedDivider(
                thickness = 20.dp,
                spacing = 10.dp,
                dashLength = 40.dp
            )
        }
    }
}*/
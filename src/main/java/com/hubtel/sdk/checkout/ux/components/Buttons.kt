package com.hubtel.sdk.checkout.ux.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.sdk.checkout.ux.theme.CheckoutTheme

@Composable
internal fun FlatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    roundness: Dp = 6.dp,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(roundness),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.5.dp,
            disabledElevation = 0.dp
        ),
        colors = colors,
        contentPadding = contentPadding
    ) { content() }
}


@Composable
internal fun CheckoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    FlatButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = CheckoutTheme.colors.colorPrimary,
            contentColor = HubtelTheme.colors.colorOnPrimary,
            disabledBackgroundColor = HubtelTheme.colors.buttonDisabled,
            disabledContentColor = HubtelTheme.colors.textDisabled
        ),
    ) {
        content.invoke(this)
    }
}

@Composable
internal fun LoadingTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    CheckoutButton(
        onClick = { if (!loading) onClick() },
        enabled = enabled,
        modifier = modifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = HubtelTheme.colors.colorOnPrimary,
                modifier = Modifier.size(30.dp)
            )
        } else {
            Text(
                text = text.uppercase(),
                style = HubtelTheme.typography.button,
                modifier = Modifier.padding(Dimens.paddingNano)
            )
        }
    }
}
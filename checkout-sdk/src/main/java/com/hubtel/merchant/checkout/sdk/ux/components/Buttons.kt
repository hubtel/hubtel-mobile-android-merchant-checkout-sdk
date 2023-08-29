package com.hubtel.merchant.checkout.sdk.ux.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme


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

@Composable
fun TealTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAllCaps: Boolean = true,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    FlatTextButton(
        text,
        onClick = onClick,
        modifier = modifier,
        textAllCaps = textAllCaps,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = HubtelTheme.colors.colorPrimary,
            contentColor = HubtelTheme.colors.colorOnPrimary,
            disabledBackgroundColor = HubtelTheme.colors.buttonDisabled,
            disabledContentColor = HubtelTheme.colors.textDisabled
        ),
        contentPadding = contentPadding
    )
}

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
internal fun FlatTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    roundness: Dp = 6.dp,
    textAllCaps: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    textStyle: TextStyle = MaterialTheme.typography.button,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    FlatButton(
        onClick = onClick,
        modifier = modifier,
        roundness = roundness,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding
    ) {
        ButtonText(text, textAllCaps, textStyle)
    }
}

@Composable
internal fun FlatOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = ButtonDefaults.outlinedBorder,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
internal fun FlatOutlineTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textAllCaps: Boolean = true,
    style: TextStyle = MaterialTheme.typography.button,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = ButtonDefaults.outlinedBorder,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    FlatOutlineButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        ButtonText(text, textAllCaps, style)
    }
}

@Composable
private fun ButtonText(
    text: String,
    textAllCaps: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.button,
) {
    Text(
        if (!textAllCaps) text else text.uppercase(),
        modifier = Modifier.padding(vertical = 8.dp),
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

val BACK_BUTTON_SIZE = 40.dp

@Composable
fun BackArrowButton(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { onBackPressed() },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.checkout_ic_arrow_back),
            contentDescription = stringResource(R.string.checkout_back),
        )
    }
}
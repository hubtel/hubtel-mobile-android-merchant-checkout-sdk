package com.hubtel.merchant.checkout.sdk.ux.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.capitalizeFirst
import java.util.*


@Composable
fun CheckoutActionDialog(
    onDismissRequest: () -> Unit,
    positiveText: String? = null,
    negativeText: String? = null,
//    properties: DialogProperties = DialogProperties(),
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    flipButtonPositions: Boolean = false,
    content: @Composable () -> Unit,
) {
    val showButtonArea = remember(positiveText, negativeText) {
        positiveText != null || negativeText != null
    }

    HBRoundedDialog(
        onDismissRequest = onDismissRequest,
//        properties = properties,
    ) {
        Column {
            // render dialog content
            content.invoke()

            // button divider line
            if (showButtonArea) HBDivider()

            if (showButtonArea) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max)
                ) {
                    if(!flipButtonPositions){
                        negativeText?.let {
                            Text(
                                text = it.capitalizeFirst(),
                                textAlign = TextAlign.Center,
                                style = HubtelTheme.typography.button,
                                color = HubtelTheme.colors.error,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clickable { onNegativeClick.invoke() }
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        positiveText?.let {
                            Text(
                                text = it.capitalizeFirst(),
                                color = CheckoutTheme.colors.colorPrimary,
                                textAlign = TextAlign.Center,
                                style = HubtelTheme.typography.button,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { onPositiveClick.invoke() }
                                    .padding(16.dp),
                            )
                        }
                    }

                    if (negativeText != null && positiveText != null) {
                        HBDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                    }

                    if(!flipButtonPositions){
                        positiveText?.let {
                            Text(
                                text = it.capitalizeFirst(),
                                color = CheckoutTheme.colors.colorPrimary,
                                textAlign = TextAlign.Center,
                                style = HubtelTheme.typography.button,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { onPositiveClick.invoke() }
                                    .padding(16.dp),
                            )
                        }
                    } else {
                        negativeText?.let {
                            Text(
                                text = it.capitalizeFirst(),
                                textAlign = TextAlign.Center,
                                style = HubtelTheme.typography.button,
                                color = HubtelTheme.colors.error,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clickable { onNegativeClick.invoke() }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutMessageDialog(
    onDismissRequest: () -> Unit,
    painter: Painter? = null,
    titleText: String? = null,
    message: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
//    properties: DialogProperties = DialogProperties(),
    flipButtonPositions: Boolean = false,
) {
    CheckoutActionDialog(
        onDismissRequest = onDismissRequest,
        positiveText = positiveText,
        negativeText = negativeText,
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
//        properties = properties,
        flipButtonPositions = flipButtonPositions
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp)
                )
            }

            if (titleText != null) {
                Text(
                    text = titleText,
                    style = HubtelTheme.typography.h4,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (message != null) {
                Text(
                    text = message,
                    style = HubtelTheme.typography.body1,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HBRoundedDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
//    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
//        properties = DialogProperties(
//            dismissOnBackPress = properties.dismissOnBackPress,
//            dismissOnClickOutside = properties.dismissOnClickOutside,
//            securePolicy = properties.securePolicy,
//            usePlatformDefaultWidth = false,
//        )
    ) {
        Card(
            modifier = modifier.fillMaxWidth(0.9f),
            elevation = 0.dp,
            shape = HubtelTheme.shapes.medium,
            backgroundColor = HubtelTheme.colors.cardBackground,
            contentColor = HubtelTheme.colors.textPrimary,
            content = content
        )
    }
}

@Composable
fun HBProgressDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = stringResource(R.string.checkout_loading),
    textColor: Color = HubtelTheme.colors.textPrimary,
    progressColor: Color = HubtelTheme.colors.colorPrimary,
    onDismissRequest: () -> Unit = {},
//    properties: DialogProperties = DialogProperties(
//        dismissOnClickOutside = false,
//        dismissOnBackPress = false,
//    ),
) {
    HBRoundedDialog(
        onDismissRequest = onDismissRequest,
//        properties = properties,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(Dimens.paddingDefault),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            CircularProgressIndicator(
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp),
                color = progressColor,
            )

            if (title != null) {
                Text(
                    text = title,
                    style = HubtelTheme.typography.body1,
                    color = textColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (message != null) {
                Text(
                    text = message,
                    style = HubtelTheme.typography.body1,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
package com.hubtel.merchant.checkout.sdk.ux.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

@Composable
fun HBTopAppBar(
    title: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    onNavigateUp: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    backgroundColor: Color = HubtelTheme.colors.uiBackground2,
    contentColor: Color = HubtelTheme.colors.textPrimary,
    elevation: Dp = 1.dp,
) {
    HBTopAppBar(
        modifier, backgroundColor,
        contentColor, elevation
    ) {
        if (onNavigateUp != null) {
            BackArrowButton(onNavigateUp)
        } else {
            Box(androidx.compose.ui.Modifier.size(BACK_BUTTON_SIZE))
        }

        CompositionLocalProvider(LocalTextStyle provides HubtelTheme.typography.h3) {
            title()
        }

        if (actions != null) {
            actions()
        } else {
            Box(androidx.compose.ui.Modifier.size(BACK_BUTTON_SIZE))
        }
    }
}

@Composable
fun HBTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = HubtelTheme.colors.uiBackground2,
    contentColor: Color = HubtelTheme.colors.textPrimary,
    elevation: Dp = 2.dp,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        elevation = elevation,
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.paddingDefault, vertical = Dimens.spacingDefault)
                .heightIn(min = 56.dp)
                .fillMaxWidth() then modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}


@Composable
fun LocationSelector(
    addressName: String?,
    color: Color = HubtelTheme.colors.textPrimary,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.checkout_ic_location_pin_black),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color),
        )

        Text(
            addressName ?: stringResource(id = R.string.checkout_near_by),
            style = HubtelTheme.typography.h4,
            modifier = Modifier
                .widthIn(max = 140.dp)
                .padding(horizontal = 4.dp),
            color = color,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )

        Image(
            painter = painterResource(id = R.drawable.checkout_ic_caret_down),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color),
        )
    }
}
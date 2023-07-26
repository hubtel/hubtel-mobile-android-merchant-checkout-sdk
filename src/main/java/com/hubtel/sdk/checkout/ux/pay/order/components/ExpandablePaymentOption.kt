package com.hubtel.sdk.checkout.ux.pay.order.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.hubtel.core_ui.components.custom.HBRadioButton
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.core_ui.theme.Teal100


@Composable
internal fun ExpandablePaymentOption(
    title: String,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    decoration: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .background(HubtelTheme.colors.cardBackground),
    ) {
        // header
        Row(
            modifier = Modifier
                .clickable { onExpand() }
                .background(if (expanded) Teal100 else Color.Transparent)
                .padding(Dimens.paddingDefault),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            HBRadioButton(
                selected = expanded,
                onClick = null,
                modifier = Modifier.padding(
                    end = Dimens.spacingDefault,
                )
            )

            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            decoration?.invoke()
        }

        AnimatedVisibility(visible = expanded) {
            content()
        }

        Divider(
            color = HubtelTheme.colors.outline,
            modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
        )
    }
}




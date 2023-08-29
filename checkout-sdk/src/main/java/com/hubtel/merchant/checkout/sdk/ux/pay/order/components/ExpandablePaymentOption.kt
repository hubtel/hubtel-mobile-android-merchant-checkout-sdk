package com.hubtel.merchant.checkout.sdk.ux.pay.order.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme


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
                .background(if (expanded) CheckoutTheme.colors.colorAccent else Color.Transparent)
                .padding(Dimens.paddingDefault),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            RadioButton(
                selected = expanded,
                onClick =  null,
                modifier = Modifier.padding(
                    end = Dimens.spacingDefault,
                ),
                colors = RadioButtonDefaults.colors(
                    selectedColor = CheckoutTheme.colors.colorPrimary,
                    unselectedColor = CheckoutTheme.colors.colorPrimary,
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
    }
}




package com.hubtel.merchant.checkout.sdk.ux.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

import com.hubtel.core_utils.extensions.formatMoneyParts


@Composable
internal fun PartedPriceText(
    price: Double?,
    currency: String?,
    modifier: Modifier = Modifier,
    includeDecimal: Boolean = false,
    style: TextStyle = HubtelTheme.typography.body1,
    color: Color = HubtelTheme.colors.textPrimary,
) {
    val priceParts = remember(price, currency, includeDecimal) {
        price.formatMoneyParts(currency, includeDecimal)
    }

    val currencyFontSize = remember(style) {
        style.fontSize.times(0.6f)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Text(
            priceParts.first,
            style = style.copy(fontSize = currencyFontSize),
            color = color,
            modifier = Modifier.padding(top = 2.dp)
        )

        Text(
            priceParts.second,
            style = style,
            color = color,
        )

        if (includeDecimal) {
            Text(
                priceParts.third,
                style = style,
                color = color,
            )
        }
    }
}
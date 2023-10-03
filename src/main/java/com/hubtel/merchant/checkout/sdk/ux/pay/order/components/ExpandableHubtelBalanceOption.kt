package com.hubtel.merchant.checkout.sdk.ux.pay.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.model.Wallet2
import com.hubtel.merchant.checkout.sdk.ux.components.PartedPriceText

@Composable
internal fun ExpandableHubtelBalanceOption(
    wallet: Wallet2?,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {

    ExpandablePaymentOption(
        title = "Hubtel Balance",
        expanded = expanded,
        onExpand = onExpand,
        decoration = {
            PartedPriceText(
                price = wallet?.currentBalance,
                currency = null,
                includeDecimal = true
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault),
        ) {
            Text(
                text = stringResource(R.string.checkout_hubtel_balance_debit_msg),
                style = HubtelTheme.typography.caption,
            )
        }
    }
}
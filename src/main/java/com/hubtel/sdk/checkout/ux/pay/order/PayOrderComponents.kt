package com.hubtel.sdk.checkout.ux.pay.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
//import com.hubtel.core_storage.model.checkout.CheckoutFee
import com.hubtel.core_ui.theme.*
import com.hubtel.sdk.checkout.R
import com.hubtel.sdk.checkout.ui.utils.formatMoneyParts

@Composable
internal fun CheckoutReceiptCard(
    logoUrl: String?,
    accountName: String,
    accountNumber: String,
    serviceName: String?,
    fees: List<CheckoutFee>,
    amount: Double,
    total: Double,
    modifier: Modifier = Modifier
) {

    val amountParts = remember(amount) { amount.formatMoneyParts(includeDecimals = true) }
    val totalParts = remember(total) { total.formatMoneyParts(includeDecimals = true) }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // zig-zag top
        Image(
            painter = painterResource(R.drawable.checkout_ic_receipt_zigzag_top),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 10.dp)
                .rotate(180f),
        )

        Column(
            modifier = Modifier
                .background(HubtelTheme.colors.cardBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
        ) {

            // service image & user info
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingDefault),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = Dimens.paddingMedium)
            ) {
                Image(
                    painter = rememberImagePainter(logoUrl),
                    contentDescription = serviceName,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.paddingNano),
                ) {
                    Text(text = accountName)

                    if (accountNumber.isNotBlank()) {
                        Text(
                            text = accountNumber,
                            color = HubtelTheme.colors.textSecondary,
                            style = HubtelTheme.typography.body2,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.spacingDefault))

            Divider(
                thickness = 1.dp,
                color = HubtelTheme.colors.outline,
                modifier = Modifier
                    .background(HubtelTheme.colors.cardBackground)
                    .padding(Dimens.paddingDefault),
            )

            // amount
            Row(
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.checkout_amount),
                    style = HubtelTheme.typography.body1,
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = amountParts.first,
                        style = HubtelTheme.typography.caption,
                    )

                    Text(
                        text = "${amountParts.second}${amountParts.third}",
                        style = HubtelTheme.typography.body1,
                        modifier = Modifier.padding(start = Dimens.paddingMicro),
                    )
                }
            }

            // fees section
            for (feeItem in fees) {
                FeeListItem(
                    title = feeItem.feeName ?: "",
                    fee = feeItem.feeAmount ?: 0.0,
                    modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                )
            }

            Spacer(Modifier.padding(vertical = Dimens.paddingNano))

            // total
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Yellow100)
                    .padding(Dimens.paddingDefault),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
            ) {
                Text(
                    text = stringResource(R.string.checkout_amount_charged_msg),
                    color = Orange700,
                )

                Row {
                    Text(
                        text = totalParts.first,
                        style = HubtelTheme.typography.body2,
                        modifier = Modifier.padding(top = Dimens.paddingMicro)
                    )

                    Text(
                        text = "${totalParts.second}${totalParts.third}",
                        style = HubtelTheme.typography.h1,
                    )
                }
            }
        }

        // zig zag bottom
        Image(
            painter = painterResource(R.drawable.checkout_ic_receipt_zigzag_top),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(Yellow100),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 10.dp),
        )
    }
}

@Composable
private fun FeeListItem(
    title: String?,
    fee: Double?,
    modifier: Modifier = Modifier
) {
    val amountParts = remember(fee) {
        fee.formatMoneyParts(includeDecimals = true)
    }

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title ?: "",
            style = HubtelTheme.typography.body1,
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = amountParts.first,
                style = HubtelTheme.typography.caption,
            )

            Text(
                text = "${amountParts.second}${amountParts.third}",
                style = HubtelTheme.typography.body1,
                modifier = Modifier.padding(start = Dimens.paddingMicro),
            )
        }
    }
}



package com.hubtel.merchant.checkout.sdk.ux.pay.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubtel.core_ui.components.custom.HBDivider
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.core_utils.extensions.formatMoneyParts
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.BusinessInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.ux.components.FlatButton
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme

@Composable
internal fun CheckoutReceiptCard(
    fees: List<CheckoutFee>,
    amount: Double,
    total: Double,
    businessInfo: BusinessInfo? = BusinessInfo(
        stringResource(R.string.checkout_business_name),
        stringResource(R.string.checkout_business_contact)
    ),
    modifier: Modifier = Modifier
) {

    val amountParts = remember(amount) { amount.formatMoneyParts(includeDecimals = true) }
    val totalParts = remember(total) { total.formatMoneyParts(includeDecimals = true) }
    var isExpanded = remember { false }

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

            // business info
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimens.paddingDefault)
                    .padding(top = Dimens.paddingLarge),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.mtn_ic_logo),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp),
                )
                Column(modifier = Modifier.padding(Dimens.paddingNano)) {
                    Text(
                        text = businessInfo?.name!!,
                        style = HubtelTheme.typography.body1,
                    )
                    Text(
                        text = businessInfo.contact!!,
                        style = HubtelTheme.typography.body1,
                    )
                }
            }

            HBDivider()

            // amount
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimens.paddingDefault)
                    .padding(top = Dimens.paddingLarge),
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
            ExpandableFeesCard(fees)
//            for (feeItem in fees) {
//                FeeListItem(
//                    title = feeItem.feeName ?: "",
//                    fee = feeItem.feeAmount ?: 0.0,
//                    modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
//                )
//            }

//            Spacer(Modifier.padding(vertical = Dimens.paddingNano))

            // total
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CheckoutTheme.colors.colorAccent)
                    .padding(Dimens.paddingDefault),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
            ) {
                Text(
                    text = stringResource(R.string.checkout_amount_charged_msg),
                    color = HubtelTheme.colors.textPrimary,
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
            colorFilter = ColorFilter.tint(CheckoutTheme.colors.colorAccent),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 10.dp),
        )
    }
}

@Preview
@Composable
private fun CheckoutReceiptCardPreview() {

    val fees = listOf<CheckoutFee>(
        CheckoutFee(10.11, 10.11, CheckoutType.RECEIVE_MONEY_PROMPT.rawValue, 0.0),
        CheckoutFee(10.11, 10.11, CheckoutType.RECEIVE_MONEY_PROMPT.rawValue, 0.0),
        CheckoutFee(10.11, 10.11, CheckoutType.RECEIVE_MONEY_PROMPT.rawValue, 0.0),
    )

    CheckoutReceiptCard(fees = fees, amount = 34.33, total = 34.33)
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

@Composable
internal fun ExpandableFeesCard(fees: List<CheckoutFee>) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = isExpanded,
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth(),
//                    .background(color = Color.LightGray),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (feeItem in fees) {
                    FeeListItem(
                        title = "Fees",
                        fee = feeItem.fees ?: 0.0,
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                    )
                }
            }

        }

        FlatButton(
            onClick = { isExpanded = !isExpanded },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        ) {
            Text(
                text = stringResource(id = R.string.checkout_view_fees),
                style = HubtelTheme.typography.h3,
                color = CheckoutTheme.colors.colorAccent.copy(alpha = 0.5f),
            )
        }
    }
}
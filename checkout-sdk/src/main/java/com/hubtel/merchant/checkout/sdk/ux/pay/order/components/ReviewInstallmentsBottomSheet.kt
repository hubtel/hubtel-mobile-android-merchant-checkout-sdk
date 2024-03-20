package com.hubtel.merchant.checkout.sdk.ux.pay.order.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

@Composable
internal fun ReviewInstallmentsBottomSheet(
    onCloseTap: () -> Unit = {},
    onConfirmTap: () -> Unit,
) {
    HBScaffold(bottomBar = {
        LoadingTextButton(
            text = "CONFIRM AND PAY",
            onClick = onConfirmTap,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(Dimens.paddingDefault),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(Dimens.paddingSmall)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Column {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Pay-In-4", fontWeight = FontWeight.Bold)
                    Image(
                        painter = painterResource(id = R.drawable.checkout_ic_x_close),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onCloseTap()
                        }
                    )
                }
                Text(text = "Please review installment terms")
                Divider(
                    color = Color(0xFF9CABB8),
                    modifier = Modifier.height(2.dp)
                )
            }

            paymentDetailEntries.forEach { entry ->
                ReviewEntryCard(entry = entry)
                Spacer(modifier = Modifier.height(Dimens.paddingDefault))
            }

            Text(
                text = "Note that the interest of GHS 80.00 will be paid\n" +
                        "on your first instalment", fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Dimens.paddingDefault))

            Text(text = "Repayment Schedule", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Dimens.paddingNano))
            Divider(
                color = Color(0xFF9CABB8),
                modifier = Modifier.height(2.dp)
            )
            repaymentScheduleEntries.forEach { entry ->
                Spacer(modifier = Modifier.height(Dimens.paddingDefault))
                ReviewEntryCard(entry = entry)
                Spacer(modifier = Modifier.height(Dimens.paddingDefault))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .background(color = Color(0xFFEDFAF7))
            ) {
                Spacer(modifier = Modifier.height(Dimens.paddingNano))
                ReviewEntryCard(
                    entry = ReviewEntry("Total", "GHS 1,080.00"),
                    titleWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Dimens.paddingNano))
                Divider(
                    color = Color(0xFF9CABB8),
                    modifier = Modifier.height(2.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
            Text(
                text = "You are obligated to make payments as outlined within this period of time. " +
                        "Failure to make payments on time may lead to increased interest rates, " +
                        "damage to your credit score and potential legal action",
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingDefault)
                    .background(
                        color = HubtelTheme.colors.cardBackground,
                        shape = HubtelTheme.shapes.medium.copy(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(0.dp),
                        )
                    )
                    .padding(top = Dimens.paddingDefault),
            )

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

data class ReviewEntry(val title: String, val detail: String)

@Composable
internal fun ReviewEntryCard(
    entry: ReviewEntry,
    modifier: Modifier = Modifier,
    titleWeight: FontWeight = FontWeight.Normal,
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.fillMaxWidth()) {
        Text(text = entry.title, fontWeight = titleWeight)

        Text(text = entry.detail, fontWeight = FontWeight.Bold)
    }
}

private val paymentDetailEntries = listOf(
    ReviewEntry("Amount", "GHS 1,000.00"),
    ReviewEntry("Interest", "GHS 80.00"),
    ReviewEntry("Interest Rate", "8%"),
)

val repaymentScheduleEntries = listOf(
    ReviewEntry("Now", "GHS 330.00"),
    ReviewEntry("12 Aug, 2023", "GHS 250.00"),
    ReviewEntry("19 Aug, 2023", "GHS 250.00"),
    ReviewEntry("25 Aug, 2023", "GHS 250.00"),
)
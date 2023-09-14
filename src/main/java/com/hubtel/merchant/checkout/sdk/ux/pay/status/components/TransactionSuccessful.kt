package com.hubtel.merchant.checkout.sdk.ux.pay.status.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton

@Composable
fun TransactionSuccessfulContent() {

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val density = LocalDensity.current.density
    val maxHeight = (screenHeight / density).dp
    HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, bottomBar = {
        Column(
            Modifier
                .animateContentSize(),
        ) {
            Divider(
                color = HubtelTheme.colors.outline,
            )

            LoadingTextButton(
                text = stringResource(R.string.checkout_done),
                onClick = {
                    // TODO: "Implement onClick"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.paddingSmall)
            )
        }
    }) {
        val constraints = ConstraintSet {
            val backgroundBox = createRefFor("backgroundBox")
            val topBox = createRefFor("topBox")

            constrain(backgroundBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(topBox) {
                top.linkTo(backgroundBox.bottom, (-40).dp)
                start.linkTo(parent.start /* goneMargin = 15.dp*/)
                end.linkTo(parent.end /* goneMargin = 15.dp*/)
            }
        }

        ConstraintLayout(
            constraints, modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeight)
                    .background(color = Color(0xFFDBF7E0))
                    .layoutId("backgroundBox")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkout_ic_success),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(Dimens.paddingNano)
                    )
                    Text(text = "Success", style = HubtelTheme.typography.h3)
                    val text = buildAnnotatedString {
                        append("GHS1000 ")
                        pushStyle(style = SpanStyle(color = Color(0xFF359846)))
                        append("has been paid")
                        pop()
                    }

                    Text(
                        text = text,
                        style = HubtelTheme.typography.body1
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(Dimens.paddingDefault)
                    .background(Color(0xFFFFF4CC), shape = RoundedCornerShape(16.dp))
                    .layoutId("topBox")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_enterprise_insurance),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(text = "Orders and Delivery", style = HubtelTheme.typography.body1)
                }
            }
        }
    }
}
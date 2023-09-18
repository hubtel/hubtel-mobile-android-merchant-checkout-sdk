package com.hubtel.merchant.checkout.sdk.ux.pay.status

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
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.components.custom.TealButton
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity

internal data class OrderPlacedScreen(val walletName: String?, val amount: Double?) : Screen {
    @Composable
    override fun Content() {
        ScreenContent()
    }

    @Composable
    private fun ScreenContent() {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val density = LocalDensity.current.density
        val maxHeight = (screenHeight / density).dp

        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(title = {
                Text(text = "Checkout")
            }, onNavigateUp = {})
        }, bottomBar = {
            Column(modifier = Modifier.animateContentSize()) {
                Divider(color = HubtelTheme.colors.outline)
                TealButton(
                    onClick = {
                        checkoutActivity?.finishWithResult()

                        recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingSmall)
                ) {
                    Text(text = "AGREE & CONTINUE")
                }
            }
        }) {
            val constraints = ConstraintSet {
                val backgroundBox = createRefFor("backgroundBox")
                val topBox = createRefFor("topBox")
                val buttonBox = createRefFor("buttonBox")

                constrain(backgroundBox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }

                constrain(topBox) {
//                top.linkTo(backgroundBox.bottom, (-40).dp)
                    top.linkTo(backgroundBox.bottom, -Dimens.paddingExtraLarge)
                    start.linkTo(parent.start /* goneMargin = 15.dp*/)
                    end.linkTo(parent.end /* goneMargin = 15.dp*/)
                }

                constrain(buttonBox) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(topBox.bottom, Dimens.paddingLarge)
                }
            }

            ConstraintLayout(
                constraints, modifier = Modifier.fillMaxSize()
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
                        Text(
                            text = "Your order has been placed",
                            style = HubtelTheme.typography.h3,
                            modifier = Modifier.padding(bottom = Dimens.paddingDefault)
                        )

                        Text(
                            text = "Your $walletName will be debited with GHS $amount after your order is confirmed  ",
                            style = HubtelTheme.typography.body1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
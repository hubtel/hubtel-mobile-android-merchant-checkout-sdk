package com.hubtel.merchant.checkout.sdk.ux.pay.gh_card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.constraintlayout.compose.Dimension
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
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity

internal data class GhCardConfirmationScreen(val cardInfo: CardInfo) : Screen {
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

        val isButtonEnabled by remember {
            mutableStateOf(true)
        }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(title = {
                Text(text = "Verification")
            }, onNavigateUp = {
            })
        }, bottomBar = {
            Column(modifier = Modifier.animateContentSize()) {
                TealButton(
                    onClick = {
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingSmall),
                    enabled = isButtonEnabled
                ) {
                    Text(
                        text = "OKAY",
                        modifier = Modifier
                            .background(color = HubtelTheme.colors.colorPrimary)
                            .animateContentSize()
                            .padding(Dimens.paddingSmall)
                    )
                }
            }
        }) {

            Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(Dimens.paddingDefault)
            ) {
                Box(modifier = Modifier.padding(Dimens.paddingDefault))
                Image(
                    painter = painterResource(R.drawable.checkout_verification_success),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = Dimens.paddingSmall)
                        .size(75.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Your account has been verified successfully",
                    style = HubtelTheme.typography.h3.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .padding(top = Dimens.paddingSmall, bottom = Dimens.paddingLarge)
                        .align(Alignment.CenterHorizontally)
                )
                GhanaCard()
            }

        }
    }

    @Composable
    private fun GhanaCard() {
        val constraints = ConstraintSet {
            val imageConstraints = createRefFor("armsCoat")

            constrain(imageConstraints) {
                top.linkTo(parent.top)
                end.linkTo(parent.end, -(Dimens.paddingDefault))
                width = Dimension.value(150.dp)
                height = Dimension.value(150.dp)
            }
        }

        ConstraintLayout(
            constraints,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFEDFAF7), shape = RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.checkout_coat_of_arms),
                contentDescription = null,
                modifier = Modifier.layoutId("armsCoat"),
            )

            CardContent()

        }
    }

    @Composable
    private fun CardContent(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Dimens.paddingDefault)) {
                Text(text = "Ghana Card Details", style = HubtelTheme.typography.h3)
                Box(modifier = Modifier.padding(Dimens.paddingSmall))
                CardLabel(label = "Full Name", info = "Frimpong Darkwa Kwame")
                Box(modifier = Modifier.padding(Dimens.paddingSmall))
                CardLabel(label = "Personal ID Number", info = "GHA-000338531-5")
                Box(modifier = Modifier.padding(Dimens.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CardLabel(label = "DOB", info = "10/06/1995")
                    CardLabel(
                        label = "Gender",
                        info = "Male",
                        modifier = Modifier.padding(end = Dimens.paddingLarge)
                    )
                }
            }
        }
    }

    @Composable
    private fun CardLabel(label: String, info: String, modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Text(text = label, style = HubtelTheme.typography.body2)
            Box(modifier = Modifier.padding(Dimens.paddingNano))
            Text(
                text = info, style = HubtelTheme.typography.h3
            )
        }
    }
}
package com.hubtel.merchant.checkout.sdk.ux.pay.gh_card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hubtel.core_ui.components.custom.HBTextField
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton

internal data class CardInfo(val firstName: String)

internal data class GhCardVerificationScreen(val cardInfo: CardInfo) : Screen {
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
//    val navigator = LocalNavigator.current
//    val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        val isButtonEnabled by remember {
            mutableStateOf(true)
        }

        var cardNumber by remember { mutableStateOf("") }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(title = {
                Text(text = "Verification")
            }, onNavigateUp = {
            })
        }, bottomBar = {
            Column(modifier = Modifier.animateContentSize()) {
                Divider(color = HubtelTheme.colors.outline)
                LoadingTextButton(
                    text = "SUBMIT", onClick = {}, enabled = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingSmall),
                )
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
                    painter = painterResource(R.drawable.checkout_ic_verification),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = Dimens.paddingSmall)
                        .size(75.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Verify your Government ID",
                    style = HubtelTheme.typography.h2.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
//                    .padding(Dimens.paddingSmall)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "A valid government-issued ID card is \nrequired to verify your account",
                    style = HubtelTheme.typography.body1.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .padding(Dimens.paddingSmall)
                        .align(Alignment.CenterHorizontally)
                )

                val text = buildAnnotatedString {
                    append("Ghana Card ")
                    pushStyle(style = SpanStyle(color = Color(0xFFFF3344)))
                    append("*")
                    pop()
                }

                Text(
                    text = text,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(bottom = Dimens.paddingNano)
                )


                HBTextField(value = cardNumber, readOnly = false, onValueChange = {
                    cardNumber = it
                }, modifier = Modifier.fillMaxWidth(), placeholder = {
                    Text(text = "ABC - XXXXXXXXXX - X")
                })

            }

        }
    }
}
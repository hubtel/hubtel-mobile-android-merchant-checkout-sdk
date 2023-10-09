package com.hubtel.merchant.checkout.sdk.ux.pay.gh_card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.HBTextField
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.text.input.GhanaCardVisualTransformation2
import kotlinx.coroutines.launch
import java.util.Locale

//internal data class CardInfo(val firstName: String)

internal class GhCardVerificationScreen(
    val config: CheckoutConfig,
    private val phoneNumber: String,
    private val checkoutType: CheckoutType? = null
) : Screen {
    @Composable
    override fun Content() {

        val viewModel = viewModel<GhCardVerificationViewModel>(
            factory = GhCardVerificationViewModel.getViewModelFactory(config.apiKey),
        )

        ScreenContent(viewModel = viewModel)
    }

    @Composable
    private fun ScreenContent(viewModel: GhCardVerificationViewModel) {

        val screenHeight = LocalConfiguration.current.screenHeightDp
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        val cardNumberFocusRequester = remember {
            FocusRequester()
        }

        var isButtonEnabled by remember {
            mutableStateOf(false)
        }

        var cardNumber by remember { mutableStateOf("") }

        val cardState by viewModel.cardUiState

        var showErrorDialog by remember { mutableStateOf(false) }
        var isButtonLoading by remember { mutableStateOf(false) }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2,
            topBar = {
                HBTopAppBar(title = {
                    Text(text = "Verification")
                }, onNavigateUp = {
                    navigator?.pop()
                })
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .animateContentSize()
                ) {
                    Divider(color = HubtelTheme.colors.outline)
                    LoadingTextButton(
                        text = "SUBMIT",
                        onClick = {
                            isButtonLoading = true
                            val card = hyphenate(cardNumber)
                            viewModel.addGhanaCard(config, phoneNumber, card)
                        },
                        enabled = isButtonEnabled,
                        loading = isButtonLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.paddingSmall),
                    )
                }
            }) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(Dimens.paddingDefault)
                    .verticalScroll(scrollState)
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


                HBTextField(
                    value = cardNumber,
                    readOnly = false,
                    onValueChange = {
                        val mediate =
                            if (it == "g" || it == "h" || it == "a") it.uppercase(Locale.ROOT) else it
                        cardNumber = mediate
                        if (cardNumber.length >= 13) {
                            isButtonEnabled = true
                        }

                    },
                    modifier = Modifier
                        .focusRequester(cardNumberFocusRequester)
                        .onFocusChanged {
                            if (it.hasFocus) {
                                coroutineScope.launch {
                                    scrollState.scrollTo(scrollState.maxValue)
                                }
                            }
                        }
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = "ABC-XXXXXXXXXX-X")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    visualTransformation = GhanaCardVisualTransformation2()
                )

                if (showErrorDialog) {
                    CheckoutMessageDialog(
                        onDismissRequest = {},
                        titleText = "Error",
                        message = cardState.error,
                        positiveText = stringResource(R.string.checkout_okay),
                        onPositiveClick = {
                            showErrorDialog = false
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = false, dismissOnClickOutside = false
                        )
                    )
                }

                // adds extra height to content to allow for scrolling
                // to avoid keyboard covering text input field
                Box(modifier = Modifier.height((screenHeight / 4).dp))

            }
        }

        LaunchedEffect(Unit) {
            cardNumberFocusRequester.requestFocus()
            scrollState.scrollTo(scrollState.maxValue)
        }

        LaunchedEffect(cardState) {
            if (cardState.success) {
                navigator?.push(
                    GhCardConfirmationScreen(
                        config,
                        phoneNumber,
                        cardNumber = "card",
                        checkoutType = checkoutType
                    )
                )
            }

            if (cardState.hasError) {
                showErrorDialog = true
                isButtonLoading = false
            }
        }
    }

    private fun hyphenate(input: String): String {
        if (input.length >= 4) {
            val prefix = input.substring(0, 3)
            val lastChar = input.last().toString()
            val middleChars = input.substring(3, input.length - 1)
            return "$prefix-$middleChars-$lastChar"
        }
        return input
    }

}
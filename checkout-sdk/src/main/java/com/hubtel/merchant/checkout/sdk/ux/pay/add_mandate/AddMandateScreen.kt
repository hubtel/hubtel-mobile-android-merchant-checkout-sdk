package com.hubtel.merchant.checkout.sdk.ux.pay.add_mandate

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.HBTextField
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.BankCardUiState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.MomoWalletUiState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.OtherPaymentUiState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderViewModel
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType
import com.hubtel.merchant.checkout.sdk.ux.pay.status.PaymentStatusScreen
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity

internal data class AddMandateScreen(
    val config: CheckoutConfig,
    val walletType: PayOrderWalletType?,
    val uiState: UiStates
) : Screen, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CheckoutConfig::class.java.classLoader)!!,
        parcel.readString()?.let {
            PayOrderWalletType.valueOf(it)
        },
        parcel.readParcelable(UiStates::class.java.classLoader)!!
    ) {
    }

    @Composable
    override fun Content() {
        val viewModel = viewModel<PayOrderViewModel>(
            factory = PayOrderViewModel.getViewModelFactory(config.apiKey),
        )

        ScreenContent(viewModel = viewModel)
    }

    @Composable
    private fun ScreenContent(viewModel: PayOrderViewModel) {

        var isLoading by remember {
            mutableStateOf(false)
        }

        var isButtonEnabled by remember {
            mutableStateOf(false)
        }

        var mandateId by remember { mutableStateOf("") }
        val mandateFocusRequester = remember { FocusRequester() }

        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        HBScaffold(topBar = {
            HBTopAppBar(title = { Text(text = "Mandate ID") }, onNavigateUp = {
                navigator?.pop()
            })
        }, bottomBar = {

            LoadingTextButton(
                text = "CONTINUE", onClick = {
                    isLoading = true

                    viewModel.updatePaymentInfo(
                        walletType!!,
                        uiState.momoWalletUiState,
                        uiState.otherPaymentUiState,
                        uiState.bankCardUiState,
                        null
                    )

                    viewModel.payOrder(config, walletType)

                    navigator?.push(
                        PaymentStatusScreen(
                            providerName = uiState.otherPaymentUiState.walletProvider?.provider,
                            config,
                            checkoutType = null
                        )
                    )
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.paddingDefault)
                    .animateContentSize(),
                enabled = isButtonEnabled,
                loading = isLoading
            )
        }) {
            Column(modifier = Modifier
                .padding(Dimens.paddingDefault)
                .verticalScroll(
                    rememberScrollState()
                )) {
                Text(text = "Mandate ID")
                Box(modifier = Modifier.padding(bottom = Dimens.paddingDefault))
                HBTextField(
                    value = mandateId,
                    readOnly = false,
                    onValueChange = {
                        if (it.isDigitsOnly()) {
                            mandateId = it
                            viewModel.mandateIdNumber = mandateId
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .focusRequester(mandateFocusRequester)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = "Enter Mandate ID")
                    },
                )
                Box(modifier = Modifier.padding(bottom = Dimens.paddingDefault))
                Text(buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(stringResource(id = R.string.checkout_gmoney_mandate_id_msg))
                    pop()
                    append(stringResource(id = R.string.checkout_gmoney_mandate_id_steps))
                }, style = HubtelTheme.typography.body2)
            }
        }

        LaunchedEffect(mandateId) {
            isButtonEnabled = mandateId.length >= 5
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(config, flags)
        parcel.writeParcelable(uiState, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddMandateScreen> {
        override fun createFromParcel(parcel: Parcel): AddMandateScreen {
            return AddMandateScreen(parcel)
        }

        override fun newArray(size: Int): Array<AddMandateScreen?> {
            return arrayOfNulls(size)
        }
    }
}

data class UiStates(
    val momoWalletUiState: MomoWalletUiState,
    val otherPaymentUiState: OtherPaymentUiState,
    val bankCardUiState: BankCardUiState
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(MomoWalletUiState::class.java.classLoader)!!,
        parcel.readParcelable(OtherPaymentUiState::class.java.classLoader)!!,
        parcel.readParcelable(BankCardUiState::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(momoWalletUiState, flags)
        parcel.writeParcelable(otherPaymentUiState, flags)
        parcel.writeParcelable(bankCardUiState, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UiStates> {
        override fun createFromParcel(parcel: Parcel): UiStates {
            return UiStates(parcel)
        }

        override fun newArray(size: Int): Array<UiStates?> {
            return arrayOfNulls(size)
        }
    }

}
package com.hubtel.merchant.checkout.sdk.ux.pay.add_wallet

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.HBTextField
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.status.PaymentStatusScreen
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity
import timber.log.Timber
import java.util.Locale

internal data class AddWalletScreen(val config: CheckoutConfig) : Screen, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CheckoutConfig::class.java.classLoader)
            ?: throw IllegalArgumentException("CheckoutConfig cannot be null")
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(config, flags)
    }

    companion object CREATOR : Parcelable.Creator<AddWalletScreen> {
        override fun createFromParcel(source: Parcel): AddWalletScreen {
            return AddWalletScreen(source)
        }

        override fun newArray(size: Int): Array<AddWalletScreen?> = arrayOfNulls(size)

    }

    @Composable
    override fun Content() {
        val viewModel =
            viewModel<AddWalletViewModel>(factory = AddWalletViewModel.getViewModelFactory(config.apiKey))
        ScreenContent(viewModel = viewModel)
    }

    private val providers = listOf(
        ProviderRes("MTN", R.drawable.checkout_ic_momo),
        ProviderRes("Vodafone", R.drawable.checkout_ic_vodafone_cash),
        ProviderRes("AirtelTigo", R.drawable.checkout_ic_airtel_tigo),
    )

    @Composable
    private fun ScreenContent(viewModel: AddWalletViewModel) {

        val userWalletUiState by viewModel.userWalletUiState

        var number by remember {
            mutableStateOf("")
        }

        val numberFocusRequester = remember {
            FocusRequester()
        }

        var selectedItem by remember { mutableStateOf(-1) }
        var provider by remember {
            mutableStateOf("")
        }

        var isButtonEnabled by remember { mutableStateOf(false) }
        var showPendingDialog by remember { mutableStateOf(false) }
        var showErrorDialog by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        HBScaffold(backgroundColor = Color.White, topBar = {
            HBTopAppBar(title = {
                Text(text = "Add Mobile Wallet")
            }, onNavigateUp = {
                navigator?.pop()
            })
        }, bottomBar = {

            LoadingTextButton(
                text = "CONTINUE", onClick = {
                    isLoading = true
                    viewModel.addUserWallet(config, number, provider)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.paddingDefault)
                    .animateContentSize(),
                enabled = isButtonEnabled,
                loading = isLoading
            )
        }) {
            Column(modifier = Modifier.padding(Dimens.paddingDefault)) {
                Box(modifier = Modifier.padding(bottom = Dimens.paddingLarge))
                Text(text = "Mobile Money Number")
                Box(modifier = Modifier.padding(bottom = Dimens.paddingDefault))
                HBTextField(
                    value = number,
                    readOnly = false,
                    onValueChange = {
                        if (it.isDigitsOnly()) number = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .focusRequester(numberFocusRequester)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = "eg 054 123 4567")
                    },
                )
                Box(modifier = Modifier.padding(bottom = Dimens.paddingDefault))
                Text(text = "Select Mobile Network")
                Box(modifier = Modifier.padding(bottom = Dimens.paddingDefault))

//                LazyRow(horizontalArrangement = Arrangement.spacedBy(space = Dimens.paddingDefault)) {
                LazyRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(providers) { index, res ->

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            val borderColor =
                                if (selectedItem == index) CheckoutTheme.colors.colorPrimary else CheckoutTheme.colors.colorAccent

                            Surface(
                                color = Color.Transparent,
                                modifier = Modifier
                                    .clickable {
                                        selectedItem = index
                                        provider = res.provider
                                        Timber.d(provider)
                                    }
                                    .clip(CircleShape)
                                    .border(
                                        Dimens.paddingMicro,
                                        borderColor,
                                        CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(res.res),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(Dimens.paddingMicro)
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(Dimens.paddingMicro, Color.White, CircleShape),
                                )
                            }
                            Text(
                                text = res.name,
                                modifier = Modifier.padding(top = Dimens.paddingDefault)
                            )
                        }

                    }
                }
            }

            if (showPendingDialog) {
                CheckoutMessageDialog(
                    onDismissRequest = {},
                    titleText = "Add Mobile Wallet",
                    message = userWalletUiState.data?.message ?: "",
                    positiveText = stringResource(R.string.checkout_okay),
                    onPositiveClick = {
                        showPendingDialog = false
                        viewModel.resetUserWalletUiState()
                        navigator?.pop()
                    },
//                    properties = DialogProperties(
//                        dismissOnBackPress = false, dismissOnClickOutside = false
//                    )
                )
            }

            if (showErrorDialog) {
                CheckoutMessageDialog(
                    onDismissRequest = {},
                    titleText = "Error",
                    message = userWalletUiState.error,
                    positiveText = stringResource(R.string.checkout_okay),
                    onPositiveClick = {
                        showErrorDialog = false
//                        navigator?.pop()
                        viewModel.resetUserWalletUiState()
                    },
//                    properties = DialogProperties(
//                        dismissOnBackPress = false, dismissOnClickOutside = false
//                    )
                )
            }
        }

        LaunchedEffect(number, provider) {
            isButtonEnabled = number.length >= 10 && provider.isNotEmpty()
        }

        LaunchedEffect(userWalletUiState) {
            if (userWalletUiState.success) {
                showPendingDialog = true
                isLoading = false
            }

            if (userWalletUiState.hasError) {
                showErrorDialog = true
                isLoading = false
            }
        }
    }


    data class ProviderRes(val name: String, val res: Int) {
        val provider: String
            get() = when (name.lowercase(Locale.ROOT)) {
                "mtn" -> "MTN"
                "vodafone" -> "Vodafone"
                "airteltigo" -> "AirtelTigo"
                else -> ""
            }
    }
}
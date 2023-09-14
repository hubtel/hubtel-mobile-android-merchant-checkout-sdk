package com.hubtel.merchant.checkout.sdk.ux.pay.order.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hubtel.core_ui.components.custom.HBDivider
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.model.OtherPaymentProvider
import com.hubtel.merchant.checkout.sdk.platform.model.OtherPaymentWallet
import com.hubtel.merchant.checkout.sdk.platform.model.WalletProvider
import com.hubtel.merchant.checkout.sdk.ux.pay.order.OtherPaymentUiState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PaymentChannel
import timber.log.Timber

@Composable
internal fun ExpandableOthersOption(
    state: OtherPaymentUiState,
    channels: List<PaymentChannel>,
    wallets: List<OtherPaymentWallet>,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
//    val otherProviders = remember(channels) {
//        listOf<WalletProvider>(WalletProvider.Hubtel, WalletProvider.ZeePay, WalletProvider.GMoney)
//    }
    val providers =
        listOf<WalletProvider>(WalletProvider.Hubtel, WalletProvider.ZeePay, WalletProvider.GMoney)

    ExpandablePaymentOption(
        title = stringResource(id = R.string.checkout_others),
        expanded = expanded,
        onExpand = {
            Timber.d("Others tapped ...")
            onExpand()
        }, decoration = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
            ) {
                providers.forEach { walletProvider ->
                    Image(
                        painter = painterResource(walletProvider.walletImages.logo),
                        contentDescription = stringResource(walletProvider.providerNameResId),
                        modifier = Modifier.height(20.dp),
                    )
                }
            }
        }, modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault)
        ) {


            PaymentMethodDropDownMenu(
                value = state.paymentProvider!!,
                onValueChange = { state.paymentProvider = it }, providers = providers)

            DropdownMenuDemo()
        }
    }
}

@Composable
fun DropdownMenuDemo() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Select an item") }

    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = selectedItem,
            onValueChange = { selectedItem = it },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = item
                        expanded = false
                    }
                ) {
                    Text(text = item)
                }
            }
        }

        Text(
            text = "Selected Item: $selectedItem",
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Toggle Dropdown")
        }
    }
}

@Composable
private fun PaymentMethodDropDownMenu(
    value: WalletProvider,
    modifier: Modifier = Modifier,
    providers: List<WalletProvider> = listOf(),
    onValueChange: (WalletProvider) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                surface = HubtelTheme.colors.cardBackground,
                onSurface = HubtelTheme.colors.textPrimary,
            )
        ) {
            DropdownMenu(expanded = expanded, onDismissRequest = {
                expanded = false
            }, modifier = modifier) {
                DropdownMenuItem(onClick = { }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = Dimens.paddingNano),
                    ) {
                        Text(text = stringResource(id = providers[0].providerNameResId))
                    }
                }
            }
        }
    }
}

@Composable
private fun OtherPaymentMethodProviderDropDownMenu(
    value: OtherPaymentWallet?,
    onValueChange: (WalletProvider) -> Unit,
    modifier: Modifier = Modifier,
    providers: List<WalletProvider> = listOf(),
    placeholder: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    var dropDownWidth by remember { mutableStateOf(0.dp) }
    var dropDownHeight by remember { mutableStateOf(0.dp) }

    Box(modifier) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                surface = HubtelTheme.colors.cardBackground,
                onSurface = HubtelTheme.colors.textPrimary,
            )
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(dropDownWidth)
            ) {
                providers.forEach { selectionOption ->
                    DropdownMenuItem(onClick = {
                        onValueChange(selectionOption)
                        expanded = false
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = Dimens.paddingNano),
                        ) {
                            Text(
                                text = stringResource(selectionOption.providerNameResId),
                                color = HubtelTheme.colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                painter = painterResource(com.hubtel.core_ui.R.drawable.core_ic_caret_right_deep),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                            )
                        }

                        if (selectionOption != providers.lastOrNull()) {
                            HBDivider(Modifier.padding(horizontal = Dimens.paddingDefault))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ExpandableOthersOption2() {

}
package com.hubtel.merchant.checkout.sdk.ux.pay.order.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import com.hubtel.merchant.checkout.sdk.platform.model.WalletProvider
import com.hubtel.merchant.checkout.sdk.ux.components.HBTextField
import com.hubtel.merchant.checkout.sdk.ux.pay.order.BankPayUiState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PaymentChannel
import com.hubtel.merchant.checkout.sdk.ux.pay.order.toOthersWalletProviders
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BankPayOption(
    state: BankPayUiState,
    channels: List<PaymentChannel>,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
    wallets: List<WalletResponse> = emptyList(),
) {
    val context = LocalContext.current

    val otherChannelProviders = remember(channels) {
        channels.toOthersWalletProviders()
    }

    val index by remember {
        mutableStateOf(0)
    }

    var walletState by remember {
        mutableStateOf(if (wallets.isEmpty()) WalletResponse() else wallets[index])
    }

    val phoneNumberFocusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    ExpandablePaymentOption(
        title = stringResource(R.string.checkout_bank_pay),
        expanded = expanded,
        onExpand = onExpand,
        decoration = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault).height(38.dp),
            ) {

//                Image(
//                    painter = painterResource(R.drawable.img),
//                    contentDescription = stringResource(R.string.checkout_bank_pay),
//                    modifier = Modifier.size(24.dp),
//                    contentScale = ContentScale.Fit
//                )

                Box(
                    modifier = Modifier.size(80.dp) // Adjust the size as needed
                ) {
                    Image(
                        painter = painterResource(R.drawable.img),
                        contentDescription = stringResource(R.string.checkout_bank_pay),
                        modifier = Modifier.fillMaxSize(), // Fill the available space in the Box
                        contentScale = ContentScale.Fit
                    )
                }
            }
        },
        modifier = modifier,
    ) {

    }


    LaunchedEffect(expanded) {
        if (!expanded) return@LaunchedEffect

        delay(500)


    }
}

@Composable
private fun WalletDropdownMenu(
    wallet: WalletResponse,
    onValueChange: (WalletResponse) -> Unit,
    modifier: Modifier = Modifier,
    wallets: List<WalletResponse?> = listOf(),
    onAddNewTapped: () -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    var dropDownWidth by remember { mutableStateOf(0.dp) }
    var dropDownHeight by remember { mutableStateOf(0.dp) }

    Box(modifier = modifier) {
        HBTextField(readOnly = true,
            value = wallet.accountNo ?: "",
            onValueChange = {},
            placeholder = placeholder,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) stringResource(R.string.checkout_close_drop_down_menu)
                        else stringResource(R.string.checkout_open_drop_down_menu),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    dropDownWidth = with(density) { it.size.width.toDp() }
                    dropDownHeight = with(density) { it.size.height.toDp() }
                })

        Box(
            modifier = Modifier
                .height(dropDownHeight)
                .width(dropDownWidth)
                .clickable { expanded = !expanded },
        )

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
                val filteredWallets = wallets.filter { it?.provider != "Hubtel" }
                filteredWallets.forEach { selectionOption ->
                    DropdownMenuItem(onClick = {
                        onValueChange(selectionOption!!)
                        expanded = false
                    }) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = Dimens.paddingNano),
                            ) {
                                selectionOption?.accountNo?.let {
                                    Text(
                                        text = it,
                                        color = CheckoutTheme.colors.colorPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Row {
                                selectionOption?.getProvider?.let {
                                    val providerName = it
                                    Text(
                                        text = providerName,
                                        color = CheckoutTheme.colors.colorAccent,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Divider(
                        color = HubtelTheme.colors.outline,
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault)
                    )
                }

                // add new number
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.paddingNano),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            onAddNewTapped()
                        }
                        .padding(horizontal = Dimens.paddingDefault, vertical = Dimens.paddingNano),
                ) {

                    Icon(
                        painter = painterResource(R.drawable.checkout_ic_add_circle_outline),
                        contentDescription = null,
                        tint = CheckoutTheme.colors.colorPrimary,
                        modifier = Modifier.size(26.dp),
                    )

                    Text(
                        text = "Add a new number",
                        color = CheckoutTheme.colors.colorPrimary,
                        style = HubtelTheme.typography.h3,
                        modifier = Modifier.weight(1f),
                    )

                    Icon(
                        painter = painterResource(R.drawable.checkout_ic_caret_right_deep),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OtherWalletProviderDownMenu(
    value: WalletProvider?,
    onValueChange: (WalletProvider) -> Unit,
    modifier: Modifier = Modifier,
    providers: List<WalletProvider> = listOf(),
    placeholder: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    var dropDownWidth by remember { mutableStateOf(0.dp) }
    var dropDownHeight by remember { mutableStateOf(0.dp) }

    Box(modifier) {
        HBTextField(readOnly = true,
            value = value?.let { stringResource(it.providerNameResId) } ?: "",
            onValueChange = {},
            placeholder = placeholder,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) stringResource(R.string.checkout_close_drop_down_menu)
                        else stringResource(R.string.checkout_open_drop_down_menu),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    dropDownWidth = with(density) { it.size.width.toDp() }
                    dropDownHeight = with(density) { it.size.height.toDp() }
                })

        Box(
            modifier = Modifier
                .height(dropDownHeight)
                .width(dropDownWidth)
                .clickable { expanded = !expanded },
        )

        // use material theme override to show correct colors on drop down
        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                surface = HubtelTheme.colors.cardBackground,
                onSurface = HubtelTheme.colors.textPrimary,
            )
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(dropDownWidth),
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
                        }
                    }

//                    if (selectionOption != providers.lastOrNull()) {
//                        HBDivider(Modifier.padding(horizontal = Dimens.paddingDefault))
//                    }
                }
            }
        }
    }
}
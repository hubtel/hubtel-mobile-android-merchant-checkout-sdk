package com.hubtel.sdk.checkout.ux.pay.order.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hubtel.core_storage.model.Wallet
import com.hubtel.core_ui.components.custom.HBTextField
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.feature_checkout.R
import com.hubtel.sdk.checkout.ux.pay.order.MomoWalletUiState
import com.hubtel.feature_wallet.extensions.WalletProvider
import com.hubtel.feature_wallet.extensions.walletProvider

@Composable
internal fun ExpandableMomoOption(
    state: MomoWalletUiState,
    wallets: List<Wallet>,
    expanded: Boolean,
    onExpand: () -> Unit,
    onAddNewNumberClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    ExpandablePaymentOption(
        title = stringResource(R.string.fc_mobile_money),
        expanded = expanded,
        onExpand = onExpand,
        decoration = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
            ) {
                Image(
                    painter = painterResource(R.drawable.fw_mtn_momo),
                    contentDescription = stringResource(R.string.fw_mtn_mobile_money),
                    modifier = Modifier.height(20.dp),
                )

                Image(
                    painter = painterResource(R.drawable.fw_airtel_tigo_money),
                    contentDescription = stringResource(R.string.fw_airtel_tigo_money),
                    modifier = Modifier.height(20.dp)
                )

                Image(
                    painter = painterResource(R.drawable.fw_vodafone_cash),
                    contentDescription = stringResource(R.string.fw_vodafone_cash),
                    modifier = Modifier.height(20.dp)
                )
            }
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault),
        ) {
            MobileWalletsDropDownMenu(
                value = state.selectedWallet,
                options = wallets,
                onValueChange = { state.updateWallet(it) },
                onAddNewNumberClick = onAddNewNumberClick,
            )

            MobileWalletProviderDownMenu(
                value = state.walletProvider,
                onValueChange = { state.walletProvider = it }
            )

            Text(
                text = stringResource(
                    R.string.fc_mobile_money_payment_info_msg,
                    state.walletProvider?.let {
                        context.getString(it.providerNameResId)
                    } ?: "",
                ),
                style = HubtelTheme.typography.body2,
            )

            if (state.walletProvider?.provider == WalletProvider.MTN.provider) {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.receive_mtn_prompt))

                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(" ${stringResource(R.string.mtn_code)}\n")
                        pop()

                        append(stringResource(R.string.select_approvals))
                    },
                    style = HubtelTheme.typography.body2,
                )

            }
        }
    }
}

@Composable
private fun MobileWalletsDropDownMenu(
    value: Wallet?,
    options: List<Wallet>,
    onValueChange: (Wallet) -> Unit,
    onAddNewNumberClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var dropDownWidth by remember { mutableStateOf(0.dp) }

    Box(modifier) {
        HBTextField(
            readOnly = true,
            value = value?.accountNumber ?: "",
            onValueChange = {},
            placeholder = placeholder,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) stringResource(com.hubtel.core_ui.R.string.close_drop_down_menu)
                        else stringResource(com.hubtel.core_ui.R.string.open_drop_down_menu),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    dropDownWidth = with(density) { it.size.width.toDp() }
                }
        )

        TextFieldOverlay(modifier = Modifier.clickable { expanded = !expanded })

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

                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(selectionOption)
                            expanded = false
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = Dimens.paddingNano),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMicro),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = selectionOption.accountNumber ?: "",
                                    color = HubtelTheme.colors.textPrimary,
                                )

                                Text(
                                    text = selectionOption.walletProvider?.providerNameResId
                                        ?.let { stringResource(it) } ?: "",
                                    color = HubtelTheme.colors.textSecondary,
                                    style = HubtelTheme.typography.caption,
                                )
                            }

                            Icon(
                                painter = painterResource(R.drawable.core_ic_caret_right_deep),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                            )
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
                            expanded = false
                            onAddNewNumberClick()
                        }
                        .padding(horizontal = Dimens.paddingDefault, vertical = Dimens.paddingNano),
                ) {

                    Icon(
                        painter = painterResource(R.drawable.fc_ic_add_circle_outline),
                        contentDescription = null,
                        tint = HubtelTheme.colors.colorPrimary,
                        modifier = Modifier.size(26.dp),
                    )

                    Text(
                        text = stringResource(R.string.fc_add_a_new_number),
                        color = HubtelTheme.colors.colorPrimary,
                        style = HubtelTheme.typography.h3,
                        modifier = Modifier.weight(1f),
                    )

                    Icon(
                        painter = painterResource(R.drawable.core_ic_caret_right_deep),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}


@Composable
private fun MobileWalletProviderDownMenu(
    value: WalletProvider?,
    onValueChange: (WalletProvider) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var dropDownWidth by remember { mutableStateOf(0.dp) }

    val momoProviders = remember {
        listOf(
            WalletProvider.MTN,
            WalletProvider.Vodafone,
            WalletProvider.Tigo,
        )
    }

    Box(modifier) {
        HBTextField(
            readOnly = true,
            value = value?.let { stringResource(it.providerNameResId) } ?: "",
            onValueChange = {},
            placeholder = placeholder,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) stringResource(com.hubtel.core_ui.R.string.close_drop_down_menu)
                        else stringResource(com.hubtel.core_ui.R.string.open_drop_down_menu),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    dropDownWidth = with(density) { it.size.width.toDp() }
                }
        )

        //todo investigate and remove
        TextFieldOverlay(modifier = Modifier.clickable { expanded = !expanded })

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

                momoProviders.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(selectionOption)
                            expanded = false
                        }
                    ) {
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
                                painter = painterResource(R.drawable.core_ic_caret_right_deep),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }

                    if (selectionOption != momoProviders.lastOrNull()) {
                        Divider(
                            color = HubtelTheme.colors.outline,
                            modifier = Modifier.padding(horizontal = Dimens.paddingDefault)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextFieldOverlay(modifier: Modifier) {
    Card(
        modifier = modifier
            .height(55.dp)
            .fillMaxWidth(),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {}
}
package com.hubtel.merchant.checkout.sdk.ux.pay.order.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.hubtel.core_ui.components.custom.HBTextField
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.GreyShade100
import com.hubtel.core_ui.theme.HubtelTheme
//import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.model.Wallet
import com.hubtel.merchant.checkout.sdk.ux.pay.order.BankCardUiState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PaymentChannel
import com.hubtel.merchant.checkout.sdk.ux.pay.order.toBankWalletProviders
import com.hubtel.merchant.checkout.sdk.ux.text.input.CreditCardVisualTransformation
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import kotlinx.coroutines.delay

@Composable
internal fun ExpandableBankCardOption(
    state: BankCardUiState,
    channels: List<PaymentChannel>,
    wallets: List<Wallet>,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val bankProviders = remember(channels) { channels.toBankWalletProviders() }

    ExpandablePaymentOption(
        title = stringResource(R.string.checkout_bank_card),
        expanded = expanded,
        onExpand = { onExpand() },
        decoration = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingDefault),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
            ) {
                bankProviders.forEach { walletProvider ->
                    Image(
                        painter = painterResource(walletProvider.walletImages.logo),
                        contentDescription = stringResource(walletProvider.providerNameResId),
                        modifier = Modifier.height(20.dp),
                    )
                }
            }
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault)
        ) {

            // toggle use saved bank card
            if (wallets.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    // use new card
                    TabChip(
                        text = stringResource(R.string.checkout_use_new_card),
                        selected = !state.useSavedBankCard,
                        onClick = {
                            state.useSavedBankCard = false
//                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapUseNewCard)
                        },
                    )

                    Spacer(Modifier.padding(Dimens.paddingNano))

                    // use saved bank card
                    TabChip(
                        text = stringResource(R.string.checkout_use_saved_card),
                        selected = state.useSavedBankCard,
                        onClick = {
                            state.useSavedBankCard = true
//                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapUseSavedCard)
                        },
                    )
                }
            }

            if (!state.useSavedBankCard) {
                NewCardInputContent(state)
            } else {
                SavedCardSelectContent(
                    state,
                    wallets = wallets,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NewCardInputContent(
    state: BankCardUiState,
    modifier: Modifier = Modifier,
) {
    val cardHolderNameFocusRequester = remember { FocusRequester() }
    val cardNumberFocusRequester = remember { FocusRequester() }
    val expiryFocusRequester = remember { FocusRequester() }
    val cvvFocusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val cardNumberIconRes by remember(state.cardNumber) {
        val cardNumber = state.cardNumber.trim()

        val res = when {
            cardNumber.startsWith("4") -> R.drawable.checkout_visa_colored
            cardNumber.startsWith("5") -> R.drawable.checkout_mastercard_colored
            else -> null
        }

        mutableStateOf(res)
    }


    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault),
        modifier = modifier.bringIntoViewRequester(bringIntoViewRequester),
    ) {

        HBTextField(
            value = state.cardHolderName,
            onValueChange = { value ->
                state.cardHolderName = value
            },
            placeholder = {
                Text(text = "Card Holder Name")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.Words,
            ),
            modifier = Modifier
                .focusRequester(cardHolderNameFocusRequester)
                .fillMaxWidth()
        )

        HBTextField(
            value = state.cardNumber,
            onValueChange = { value ->
                val inputText = value.filter { it.isDigit() }.take(16)
                // take first 16 digits
                state.cardNumber = inputText
                if (inputText.length == 16) {
                    expiryFocusRequester.requestFocus()
                }
            },
            placeholder = {
                Text(text = "1234 **** **** 7890")
            },
            trailingIcon = {
                cardNumberIconRes?.let { res ->
                    Image(
                        painter = painterResource(res),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CreditCardVisualTransformation(),
            modifier = Modifier
                .focusRequester(cardNumberFocusRequester)
                .fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingDefault)
        ) {
            HBTextField(
                value = state.monthYear,
                onValueChange = { value ->
                    val inputText = value.text.filter { it.isDigit() }
                    val formatted = if (inputText.length > 2) {
                        val mm = inputText.substring(0, 2)
                        val yy = inputText.substring(2, inputText.length)
                        val isValidMonth = (mm.toIntOrNull() ?: 0) <= 12
                        if (isValidMonth) "${mm}/${yy}" else mm
                    } else inputText

                    state.monthYear = value.copy(
                        text = formatted,
                        selection = TextRange(inputText.length + 1)
                    )

                    if (formatted.length == 5) {
                        cvvFocusRequester.requestFocus()
                    }
                },
                placeholder = {
                    Text(text = stringResource(R.string.checkout_mm_yy))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .focusRequester(expiryFocusRequester)
                    .weight(1f)
            )

            HBTextField(
                value = state.cvv,
                onValueChange = { value ->
                    val inputText = value.filter { it.isDigit() }
                    if (inputText.length <= 3) {
                        state.cvv = inputText
                    }
                },
                placeholder = {
                    Text(text = stringResource(R.string.checkout_cvv))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier
                    .focusRequester(cvvFocusRequester)
                    .weight(1f)
            )
        }

        /* Row(
             verticalAlignment = Alignment.CenterVertically,
             modifier = Modifier.clickable {
                 state.saveForLater = state.saveForLater.not()
             },
         ) {
             Checkbox(
                 checked = state.saveForLater,
                 onCheckedChange = null,
                 colors = CheckboxDefaults.colors(
                     checkedColor = CheckoutTheme.colors.colorPrimary,
                     uncheckedColor = CheckoutTheme.colors.colorPrimary,
                     checkmarkColor = HubtelTheme.colors.colorOnPrimary,
                     disabledColor = HubtelTheme.colors.outline,
                 )
             )

             Text(
                 text = stringResource(R.string.checkout_card_future_use_mgs),
                 modifier = Modifier
                     .weight(1f)
                     .padding(start = Dimens.spacingDefault),
             )
         }*/
    }


    LaunchedEffect(Unit) {
        delay(500)
        cardHolderNameFocusRequester.requestFocus()
        bringIntoViewRequester.bringIntoView()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SavedCardSelectContent(
    state: BankCardUiState,
    wallets: List<Wallet>,
    modifier: Modifier = Modifier
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier.bringIntoViewRequester(bringIntoViewRequester),
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingDefault),
    ) {
        BankCardWalletsDropDownMenu(
            value = state.selectedWallet,
            options = wallets,
            onValueChange = { state.selectedWallet = it },
        )
    }

    LaunchedEffect(Unit) {
        delay(500)
        bringIntoViewRequester.bringIntoView()
    }
}


@Composable
private fun BankCardWalletsDropDownMenu(
    value: Wallet?, options: List<Wallet>,
    onValueChange: (Wallet) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var dropDownWidth by remember { mutableStateOf(0.dp) }
    var dropDownHeight by remember { mutableStateOf(0.dp) }

    val selectedCardEnding = remember(value) {
        (value?.accountNumber ?: "****").takeLast(4)
    }

    Box(modifier) {
        HBTextField(
            readOnly = true,
            value = stringResource(
                R.string.checkout_card_ending_with_format,
                selectedCardEnding,
            ),
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
                }
        )

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

                options.forEach { selectionOption ->
                    val cardIssuer = remember(selectionOption) {
                        selectionOption.cardIssuer
                    }

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
                                    text = selectionOption.hashedAccountNumber,
                                    color = HubtelTheme.colors.textPrimary,
                                )

                                Text(
                                    text = selectionOption.expiry ?: "",
                                    color = HubtelTheme.colors.textSecondary,
                                    style = HubtelTheme.typography.caption,
                                )
                            }

                            Image(
                                painter = painterResource(cardIssuer.logo),
                                contentDescription = stringResource(cardIssuer.issuerName),
                                modifier = Modifier.height(20.dp),
                            )

                            Icon(
                                painter = painterResource(R.drawable.checkout_ic_caret_right_deep),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }

                    if (selectionOption != options.lastOrNull()) {
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
private fun TabChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true
) {
    Text(
        text = text,
        color = if (selected) {
            HubtelTheme.colors.colorOnPrimary
        } else HubtelTheme.colors.textPrimary,
        style = if (selected) {
            HubtelTheme.typography.h5
        } else HubtelTheme.typography.caption,
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                if (enabled) {
                    onClick()
                }
            }
            .background(
                color = if (selected) {
                    CheckoutTheme.colors.colorPrimary
                } else GreyShade100,
                shape = CircleShape,
            )
            .padding(Dimens.paddingNano)
    )
}


internal val Wallet.hashedAccountNumber: String
    get() {
        val accountNo = accountNumber ?: ""

        if (accountNo.length >= 16) {
            val first = accountNo.take(4)
            val lastDigits = accountNo.takeLast(4)

            return "$first **** **** $lastDigits"
        }

        return accountNo
    }

internal val Wallet.cardIssuer: CardIssuer
    get() {
        val accountNo = accountNumber ?: ""

        return when {
            accountNo.startsWith("4") -> CardIssuer.Visa
            accountNo.startsWith("5")
                    || accountNo.startsWith("2") -> CardIssuer.Mastercard

            else -> CardIssuer.Visa
        }
    }

internal enum class CardIssuer(
    @DrawableRes val logo: Int,
    @StringRes val issuerName: Int
) {
    Visa(R.drawable.checkout_visa_colored, R.string.checkout_visa),
    Mastercard(R.drawable.checkout_mastercard_colored, R.string.checkout_mastercard),
}
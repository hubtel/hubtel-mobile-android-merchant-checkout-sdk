package com.hubtel.sdk.checkout.ux.pay.order

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.hubtel.sdk.checkout.model.Wallet
import com.hubtel.sdk.checkout.model.WalletProvider
import com.hubtel.sdk.checkout.model.walletProvider
import com.hubtel.sdk.checkout.platform.data.source.db.model.DbWallet

//import com.hubtel.core_storage.database.model.DbWallet
//import com.hubtel.core_storage.model.Wallet
//import com.hubtel.feature_wallet.extensions.WalletProvider
//import com.hubtel.feature_wallet.extensions.walletProvider

// NB: Steps are arranged based on order and should not be
// refactored (unless new steps are being introduced).
internal enum class CheckoutStep {
    GET_FEES,
    PAY_ORDER,
    PIN_PROMPT,
    CARD_SETUP,
    COLLECT_DEVICE_INFO,
    CHECKOUT,
    VERIFY_CARD,
    CHECKOUT_SUCCESS_DIALOG,
    PAYMENT_STATUS;
}

internal class PaymentWalletUiState {
    var payOrderWalletType by mutableStateOf<PayOrderWalletType?>(null)
        private set

    val isMomoWallet by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.MOBILE_MONEY
    }

    val isBankCard by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.BANK_CARD
    }

    val isHubtelBalance by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.HUBTEL_BALANCE
    }

    fun setWalletType(type: PayOrderWalletType?) {
        payOrderWalletType = type
    }
}

internal class BankCardUiState(
    wallet: Wallet? = null,
    useSavedBankCard: Boolean = false,
) {

    var useSavedBankCard by mutableStateOf(useSavedBankCard)

    var cardNumber by mutableStateOf("")

    var monthYear by mutableStateOf(TextFieldValue(text = ""))

    var cvv by mutableStateOf("")

    var saveForLater by mutableStateOf(false)

    var selectedWallet by mutableStateOf<Wallet?>(wallet)

    val isValid: Boolean
        get() {
            return if (useSavedBankCard) {
                selectedWallet != null
            } else {
                cardNumber.length == 16
                        && monthYear.text.length == 5
                        && cvv.length == 3
            }
        }

    override fun toString(): String {
        return """
            ${this.javaClass.simpleName}(
                useSavedBankCard = $useSavedBankCard 
                cardNumber = $cardNumber
                monthYear = $monthYear
                cvv = $cvv
                saveForLater = $saveForLater
                selectedWallet = $selectedWallet
            )
        """.trimIndent()
    }
}

internal class MomoWalletUiState(
    wallet: Wallet? = null,
) {

    var selectedWallet by mutableStateOf<Wallet?>(wallet)
        private set

    var walletProvider by mutableStateOf<WalletProvider?>(wallet?.walletProvider)

    val isValid
        get() = selectedWallet != null
                && walletProvider != null

    fun updateWallet(wallet: Wallet?) {
        selectedWallet = wallet
        walletProvider = wallet?.walletProvider
    }
}

internal data class Verfication3dsState(
    val jwt: String?,
    val customData: String?,
)

internal data class PaymentInfo(
    val walletId: Int?,
    val accountNumber: String?,
    val paymentType: String,
    val expiryMonthYear: String? = null,
    val cvv: String? = null,
    val providerName: String? = null,
    val channel: String? = null,
    val saveForLater: Boolean = false
) {

    val middle: String?
        get() {
            return if (accountNumber?.length == 16) {
                accountNumber.substring(6..11)
            } else null
        }

    val expiryMonth: String?
        get() {
            return expiryMonthYear?.split("/")
                ?.getOrNull(0)
        }

    val expiryYear: String?
        get() {
            return expiryMonthYear?.split("/")
                ?.getOrNull(1)
        }

    val fullExpiryYear: String
        get() {
            return "20${this.expiryYear}"
        }

    fun toWallet(): DbWallet {
        return DbWallet(
            id = accountNumber ?: "",
            customerID = "",
            accountName = "",
            accountNumber = accountNumber,
            type = "card",
            providerID = "",
            provider = "",
            providerType = "",
            status = "active",
            expiry = expiryMonthYear,
            currentBalance = "",
            availableBalance = "",
            walletImageUrl = "",
            secret = cvv,
            cvv = cvv,
            countryCode = "",
            hasGateKeeperPass = true,
            createdAt = "",
            updateAt = "",
            isLocalWallet = true
        )
    }
}

internal enum class PayOrderWalletType {
    MOBILE_MONEY,
    BANK_CARD,
    HUBTEL_BALANCE;
}

internal val PayOrderWalletType.paymentTypeName: String
    get() {
        return when (this) {
            PayOrderWalletType.MOBILE_MONEY -> "mobilemoney"
            PayOrderWalletType.BANK_CARD -> "card"
            PayOrderWalletType.HUBTEL_BALANCE -> "hubtel"
        }
    }

val WalletProvider.channelName: String
    get() {
        return when {
            provider.contains("vodafone", ignoreCase = true) -> {
                "${provider.lowercase()}-gh-ussd"
            }

            provider.contains("airtel", ignoreCase = true) -> {
                "tigo-gh"
            }

            else -> "${provider.lowercase()}-gh"
        }
    }

internal data class ThreeDSSetupState(
    val accessToken: String?,
    val referenceId: String?,
)


package com.hubtel.merchant.checkout.sdk.ux.pay.order

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseOrderItem
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.DbWallet
import com.hubtel.merchant.checkout.sdk.platform.model.Wallet
import com.hubtel.merchant.checkout.sdk.platform.model.WalletProvider
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ReviewEntry
import java.io.Serializable

// NB: Steps are arranged based on order and should not be
// refactored/re-ordered (unless new steps are being introduced).
internal enum class CheckoutStep {
    GET_FEES,
    PAY_ORDER,
    CARD_SETUP,
    COLLECT_DEVICE_INFO,
    CHECKOUT,
    VERIFY_CARD,
    CHECKOUT_SUCCESS_DIALOG,
    PAYMENT_COMPLETED,
    GHANA_CARD_VERIFICATION,
    SELECT_PAYMENT_METHOD;
}

internal class PaymentWalletUiState(
    walletType: PayOrderWalletType? = null,
) {
    var payOrderWalletType by mutableStateOf<PayOrderWalletType?>(walletType)
        private set

    val isBankCard by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.BANK_CARD
    }

    val isMomoWallet by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.MOBILE_MONEY
    }

    val isOtherPaymentWallet by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.OTHER_PAYMENT
    }

    val isBankPay by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.BANK_PAY
    }

    val isPayIn4 by derivedStateOf {
        payOrderWalletType == PayOrderWalletType.PAY_IN_FOUR
    }

    fun setWalletType(type: PayOrderWalletType?) {
        payOrderWalletType = type
    }
}

class MomoWalletUiState {

    var mobileNumber by mutableStateOf<String?>(null)

    var walletProvider by mutableStateOf<WalletProvider?>(WalletProvider.MTN)

    var isWalletSelected by mutableStateOf(false)

    val isValid
        get() = ((mobileNumber?.length ?: 0) >= 9
                && walletProvider != null) || ((mobileNumber?.length ?: 0) >= 9 && isWalletSelected)
}

class OtherPaymentUiState {
    var mobileNumber by mutableStateOf<String?>(null)
    var accountName by mutableStateOf<String?>(null)
    var isHubtelInternalMerchant by mutableStateOf<Boolean?>(true)
    var walletProvider by mutableStateOf<WalletProvider?>(if (isHubtelInternalMerchant == true) WalletProvider.Hubtel else WalletProvider.GMoney)

    var newMandate by mutableStateOf(false)

    var isWalletSelected by mutableStateOf(false)

    var saveForLater by mutableStateOf(false)

    val isValid
        get() = ((mobileNumber?.length ?: 0) >= 9
                && walletProvider != null) || ((mobileNumber?.length
            ?: 0) >= 9 && isWalletSelected) || (walletProvider != null && isWalletSelected)
}

class BankPayUiState {
    var isWalletSelected by mutableStateOf(false)
    var mobileNumber by mutableStateOf<String?>(null)
    var walletProvider by mutableStateOf<WalletProvider?>(WalletProvider.BankPay)

    val isValid
        get() = isWalletSelected
}

class PayIn4UiState {
    var isWalletSelected by mutableStateOf(false)
    var isMomoWalletSelected by mutableStateOf(false)
    var walletProvider by mutableStateOf<WalletProvider?>(WalletProvider.MTN)
    var mobileNumber by mutableStateOf<String?>(null)
    var repaymentEntries by mutableStateOf<List<ReviewEntry>?>(emptyList())

    val isValid
        get() = isWalletSelected
}

class BankCardUiState constructor(
    wallet: Wallet? = null,
    useSavedBankCard: Boolean = false,
) {

    var useSavedBankCard by mutableStateOf(useSavedBankCard)

    var cardHolderName by mutableStateOf("")

    var cardNumber by mutableStateOf("")

    var monthYear by mutableStateOf(TextFieldValue(text = ""))

    var cvv by mutableStateOf("")

    var saveForLater by mutableStateOf(false)

    var isInternalMerchant by mutableStateOf(false)

    var selectedWallet by mutableStateOf<Wallet?>(wallet)

    var isValidYear by mutableStateOf(false)

    val isValid: Boolean
        get() {
            return if (useSavedBankCard) {
                selectedWallet != null
            } else {
                val commonConditions = cardNumber.length == 16
                        && monthYear.text.length == 5
                        && cvv.length == 3

                return if (!isInternalMerchant) {
                    commonConditions && cardHolderName.isNotBlank()
                } else {
                    commonConditions
                }
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

internal data class Verification3dsState(
    val jwt: String?,
    val customData: String?,
)

internal data class PaymentInfo(
    val walletId: String?,
    val accountName: String?,
    val accountNumber: String?,
    val paymentType: String,
    val expiryMonthYear: String? = null,
    val cvv: String? = null,
    val providerName: String? = null,
    val channel: String? = null,
    val saveForLater: Boolean = false,
    val mandateId: String? = null
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
            id = 0L,
            accountName = accountName,
            accountNumber = accountNumber,
            provider = providerName,
            expiry = expiryMonthYear,
            cvv = cvv,
        )
    }
}

internal enum class PayOrderWalletType {
    MOBILE_MONEY,
    BANK_CARD,
    OTHER_PAYMENT,
    BANK_PAY,
    PAY_IN_FOUR
}

internal val PayOrderWalletType.paymentTypeName: String
    get() {
        return when (this) {
            PayOrderWalletType.MOBILE_MONEY -> "mobilemoney"
            PayOrderWalletType.BANK_CARD -> "card"
            PayOrderWalletType.OTHER_PAYMENT -> "others"
            PayOrderWalletType.BANK_PAY -> "bankpay"
            PayOrderWalletType.PAY_IN_FOUR -> "payin4"
        }
    }

// TODO: Add other payment methods' channels
internal val WalletProvider.channelName: String
    get() {
        return when {
            provider.contains("vodafone", ignoreCase = true) -> {
//                "${provider.lowercase()}-gh-ussd"
                "${provider.lowercase()}-gh-direct-debit"
            }

            provider.contains("airtel", ignoreCase = true) -> {
                "tigo-gh"
            }

            provider.contains("hubtel", ignoreCase = true) -> {
                "hubtel-gh"
            }

            provider.contains("g-money", ignoreCase = true) -> {
                "g-money"
            }

            provider.contains("zeepay", ignoreCase = true) -> {
                "zeepay"
            }

            provider.contains("bankpay", ignoreCase = true) -> {
                provider
            }

            else -> "${provider.lowercase()}-gh-direct-debit"
//            else -> "mtn-gh-direct-debit"
        }
    }

//internal val WalletProvider.channelName: String
//    get() {
//        return when {
//            provider.contains("vodafone", ignoreCase = true) -> {
//                "${provider.lowercase()}-gh-ussd"
//            }
//
//            provider.contains("airtel", ignoreCase = true) -> {
//                "tigo-gh"
//            }
//
//            else -> "${provider.lowercase()}-gh"
//        }
//    }

internal data class ThreeDSSetupState(
    val accessToken: String?,
    val referenceId: String?,
)

internal enum class PaymentChannel(val rawValue: String) {
    VISA("cardnotpresent-visa"),
    MASTERCARD("cardnotpresent-mastercard"),

    //    MTN("mtn-gh"),
    MTN("mtn-gh-direct-debit"),
    VODAFONE("vodafone-gh-direct-debit"),

    //    VODAFONE("vodafone-gh"),
    AIRTEL_TIGO("tigo-gh"),
    G_MONEY("g-money"),
    ZEE_PAY("zeepay"),
    HUBTEL("hubtel-gh");
}

internal fun List<String>.toPaymentChannels(): List<PaymentChannel> {
    return mapNotNull { channelName ->
        when (channelName.lowercase()) {
            PaymentChannel.VISA.rawValue -> PaymentChannel.VISA
            PaymentChannel.MASTERCARD.rawValue -> PaymentChannel.MASTERCARD
            PaymentChannel.MTN.rawValue, "mtn-gh" -> PaymentChannel.MTN
            PaymentChannel.VODAFONE.rawValue, "vodafone-gh" -> PaymentChannel.VODAFONE
            PaymentChannel.AIRTEL_TIGO.rawValue -> PaymentChannel.AIRTEL_TIGO
            PaymentChannel.HUBTEL.rawValue -> PaymentChannel.HUBTEL
            PaymentChannel.G_MONEY.rawValue -> PaymentChannel.G_MONEY
            PaymentChannel.ZEE_PAY.rawValue -> PaymentChannel.ZEE_PAY
            else -> null
        }
    }
}

internal fun List<PaymentChannel>.toMomoWalletProviders(): List<WalletProvider> {
    return mapNotNull { channel ->
        when (channel) {
            PaymentChannel.MTN -> WalletProvider.MTN
            PaymentChannel.AIRTEL_TIGO -> WalletProvider.Tigo
            PaymentChannel.VODAFONE -> WalletProvider.Vodafone
            else -> null
        }
    }
}

internal fun List<PaymentChannel>.toBankWalletProviders(): List<WalletProvider> {
    return mapNotNull { channel ->
        when (channel) {
            PaymentChannel.VISA -> WalletProvider.Visa
            PaymentChannel.MASTERCARD -> WalletProvider.Mastercard
            else -> null
        }
    }
}

internal fun List<PaymentChannel>.toOthersWalletProviders(): List<WalletProvider> {
    return mapNotNull { channel ->
        when (channel) {
            PaymentChannel.G_MONEY -> WalletProvider.GMoney
            PaymentChannel.ZEE_PAY -> WalletProvider.ZeePay
            PaymentChannel.HUBTEL -> WalletProvider.Hubtel
            else -> null
        }
    }
}

internal fun List<PaymentChannel>.toPayIn4WalletProviders(): List<WalletProvider> {
    return mapNotNull { channel ->
        when (channel) {
            PaymentChannel.MTN -> WalletProvider.MTN
            PaymentChannel.VODAFONE -> WalletProvider.Vodafone
            PaymentChannel.VISA -> WalletProvider.Visa
            PaymentChannel.MASTERCARD -> WalletProvider.Mastercard
            else -> null
        }
    }
}

internal fun CheckoutConfig.toPurchaseOrderItem(): PurchaseOrderItem {
    return PurchaseOrderItem(
        itemId = this.clientReference,
        name = this.description,
        provider = posSalesId,
        quantity = 1,
        amount = this.amount,
    )
}


// TODO: Move to response package
internal data class BusinessResponseInfo(
    val businessID: String?,
    val businessName: String?,
    val businessLogoURL: String?,
    val requireNationalID: Boolean?,
    val isHubtelInternalMerchant: Boolean?
) : Serializable
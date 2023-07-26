package com.hubtel.sdk.checkout.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.hubtel.sdk.checkout.R

//enum class WalletProvider(
//    val provider: String,
////    @StringRes val providerNameResId: Int,
////    val walletImages: WalletImages,
//) {
//    MTN("mtn"),
//    Vodafone("vodafone"),
//    AirtelTigo("airtel"),
//    Tigo("tigo"),
////    Hubtel("hubtel", R.string.fw_hubtel, WalletImages.Hubtel),
//}
//
//data class Wallet(
//    val id: String,
//    val customerID: String?,
//    val accountName: String?,
//    val accountNumber: String?,
//    val type: String?,
//    val providerID: String?,
//    val provider: String?,
//    val providerType: String?,
//    val status: String?,
//    val expiry: String?,
//    val currentBalance: Double?,
//    val availableBalance: Double?,
//    val secret: String?,
//    val countryCode: String?,
//    val walletImageUrl: String?,
//    val hasGateKeeperPass: Boolean?,
//    val createdAt: String?,
//    val updateAt: String?,
//    val isLocalWallet: Boolean? = false,
//)
//
//internal data class ThreeDSSetupState(
//    val accessToken: String?,
//    val transactionId: String?,
//)

//internal enum class CheckoutStep {
//    GET_FEES,
//    PAY_ORDER,
//    PIN_PROMPT,
//    CARD_SETUP,
//    COLLECT_DEVICE_INFO,
//    CHECKOUT,
//    VERIFY_CARD,
//    CHECKOUT_SUCCESS_DIALOG,
//    PAYMENT_STATUS;
//}

//internal class BankCardUiState(
//    wallet: Wallet? = null,
//    useSavedBankCard: Boolean = false,
//) {
//
//    var useSavedBankCard by mutableStateOf(useSavedBankCard)
//
//    var cardNumber by mutableStateOf("")
//
//    var cardName by mutableStateOf("")
//
//    var monthYear by mutableStateOf(TextFieldValue(text = ""))
//
//    var cvv by mutableStateOf("")
//
//    var saveForLater by mutableStateOf(false)
//
//    var selectedWallet by mutableStateOf<Wallet?>(wallet)
//
//    val isValid: Boolean
//        get() {
//            return if (useSavedBankCard) {
//                selectedWallet != null
//            } else {
//                cardNumber.length == 16
//                        && cardName.isNotEmpty()
//                        && monthYear.text.length == 7
//                        && cvv.length == 3
//            }
//        }
//
//    val month:String
//        get() {
//            return monthYear.text.split("/")[0]
//        }
//
//    val year:String
//        get() {
//            return monthYear.text.split("/")[1]
//        }
//
//    override fun toString(): String {
//        return """
//            ${this.javaClass.simpleName}(
//                useSavedBankCard = $useSavedBankCard
//                cardNumber = $cardNumber
//                monthYear = $monthYear
//                cvv = $cvv
//                saveForLater = $saveForLater
//                selectedWallet = $selectedWallet
//            )
//        """.trimIndent()
//    }
//}
//
//internal data class Verfication3dsState(
//    val jwt: String?,
//    val customData: String?,
//)
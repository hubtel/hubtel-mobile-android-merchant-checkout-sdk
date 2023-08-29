package com.hubtel.merchant.checkout.sdk.ux.shared

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface BaseAppNavigator {

    fun openHomeActivity(clearStack: Boolean = true)

    fun openOnBoardingPage(clearStack: Boolean = true)

    fun openEditWallet(walletId: String)

    fun openAddBankCard()

    fun openHardUpdateActivity(clearStack: Boolean = true)

    fun openAddMomoWallet()

    fun openMyAccount()

    fun openCouponsAndRewards()

    fun openWallets()

    fun openProfile()

    fun openAccountVerification()

    fun goToPurchasedOrderItems(orderId: String, activityContext: Context)

    fun openGHQR(isLogin: Boolean)

    fun openOldCareforce()

    fun openHelpChat()

    fun openPaymentsHistory()

    fun openTransactionSummary()

    fun openTermsAndConditions()

    fun openHubtelBalance()

    fun openLoyaltyProfilePage()

    fun openSenderNickname()

    fun openSecurityPage()

    fun openHubtelWalletStatement()

    fun openTakePaymentAccount()

    fun openPendingStatusTracking(orderId: String)

    fun startLegacyServiceFlow(
        serviceId: String?,
        serviceLogo: String?,
        serviceName: String?,
        businessId: String?,
        branchId: String?
    )

    suspend fun openOrderReceipt(
        orderId: String
    ): Flow<Boolean>

    fun launchDeepLink(uri : Uri)
}
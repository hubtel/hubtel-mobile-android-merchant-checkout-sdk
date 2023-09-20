package com.hubtel.merchant.checkout.sdk.platform.data.source.repository

import com.hubtel.merchant.checkout.sdk.network.ResultListWrapper
import com.hubtel.merchant.checkout.sdk.network.ResultWrapper2
import com.hubtel.merchant.checkout.sdk.network.repository.Repository
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.CheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.DbWallet
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PaymentChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CheckoutRepository(
    private val database: CheckoutDB,
    private val checkoutApiService: CheckoutApiService,
    private val checkoutPrefManager: CheckoutPrefManager,
) : Repository() {

    suspend fun apiSetup3DS(
        salesId: String,
        req: ThreeDSSetupReq
    ): ResultWrapper2<ThreeDSSetupInfo> = makeRequestToApi {
        checkoutApiService.setup3DS(salesId, req)
    }

    suspend fun apiEnroll3DS(
        salesId: String,
        transactionId: String
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        checkoutApiService.enroll3DS(salesId, transactionId)
    }

    suspend fun apiReceiveMoneyPrompt(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        checkoutApiService.receiveReceiveMoneyPrompt(salesId, req)
    }

    suspend fun apiReceiveMobileMoney(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        checkoutApiService.receiveReceiveMoneyPrompt(salesId, req)
    }

    suspend fun apiReceiveMobileMoneyDirectDebit(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        checkoutApiService.receiveMobileMoneyDirectDebit(salesId, req)
    }

    suspend fun apiReceiveMoneyPreapproval(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        checkoutApiService.receiveMoneyPreapprovalConfirm(
            salesId = salesId,
            channel = req.channel,
            customerMsisdn = req.customerMsisdn,
            clientReference = req.clientReference,
        )
    }

    suspend fun getFees(
        salesId: String,
        req: GetFeesReq,
    ): ResultWrapper2<CheckoutFee> = makeRequestToApi {
        checkoutApiService.getFees(salesId, req)
    }

    suspend fun getFeesDirectDebit(
        salesId: String,
        req: GetFeesReq,
    ): ResultWrapper2<CheckoutFee> = makeRequestToApi {
        checkoutApiService.getFeesDirectDebitCall(salesId, req.channel, req.amount)
    }

    suspend fun getTransactionStatus(
        salesId: String,
        clientReference: String,
    ): ResultWrapper2<TransactionStatusInfo> = makeRequestToApi {
        checkoutApiService.getTransactionStatus(salesId, clientReference)
    }

    suspend fun getTransactionStatusDirectDebit(
        salesId: String,
        clientReference: String,
    ): ResultWrapper2<TransactionStatusInfo> = makeRequestToApi {
        checkoutApiService.getTransactionStatusDirectDebit(salesId, clientReference)
    }

    suspend fun verifyTop(
        salesId: String,
        req: OtpReq
    ): ResultWrapper2<OtpResponse> = makeRequestToApi {
        checkoutApiService.verifyOtp(salesId, req)
    }

    suspend fun getCustomerWallets(
        salesId: String?,
        phoneNumber: String?
    ): ResultListWrapper<WalletResponse> = makeRequestToApi {
        checkoutApiService.getWallets(salesId, phoneNumber)
    }

    suspend fun getBusinessPaymentChannels(
        salesId: String,
    ): ResultWrapper2<List<String>> = makeRequestToApi {
        checkoutApiService.getBusinessPaymentChannels(salesId)
    }

    fun getPaymentChannels(): List<PaymentChannel> {
        return checkoutPrefManager.allowedPaymentChannels ?: emptyList()
    }

    fun savePaymentChannels(channels: List<PaymentChannel>) {
        checkoutPrefManager.allowedPaymentChannels = channels
    }

    suspend fun getWallets(): List<DbWallet> {
        return withContext(Dispatchers.IO) {
            database.walletDao().getAllWallets()
        }
    }

    fun saveCard(wallet: DbWallet) {
        CoroutineScope(Dispatchers.IO).launch {
            database.walletDao().insert(wallet)
        }
    }
}
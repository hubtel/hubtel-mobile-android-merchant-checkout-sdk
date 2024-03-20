package com.hubtel.merchant.checkout.sdk.platform.data.source.repository

import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.network.ResultWrapper
import com.hubtel.merchant.checkout.sdk.network.ResultWrapper2
import com.hubtel.merchant.checkout.sdk.network.repository.Repository
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.UnifiedCheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.CardReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.UserWalletReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.GhanaCardResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentChannelResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.UserWalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.DbWallet
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.HubtelWallet
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PaymentChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class UnifiedCheckoutRepository(
    private val database: CheckoutDB,
    private val unifiedCheckoutApiService: UnifiedCheckoutApiService,
    private val checkoutPrefManager: CheckoutPrefManager,
) : Repository() {
    suspend fun apiSetup3DS(
        salesId: String, req: ThreeDSSetupReq
    ): ResultWrapper2<ThreeDSSetupInfo> = makeRequestToApi {
        unifiedCheckoutApiService.setup3DS(salesId, req)
    }

    suspend fun apiEnroll3DS(
        salesId: String, transactionId: String
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        unifiedCheckoutApiService.enroll3DS(salesId, transactionId)
    }

    suspend fun apiReceiveMobilePrompt(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        unifiedCheckoutApiService.receiveMoneyPrompt(salesId, req)
    }

    suspend fun apiDirectDebit(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        unifiedCheckoutApiService.mobileMoneyDirectDebit(salesId, req)
    }

    suspend fun apiPreapproval(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        unifiedCheckoutApiService.mobileMoneyPreapproval(
            salesId = salesId,
            channel = req.channel,
            customerMsisdn = req.customerMsisdn,
            clientReference = req.clientReference,
        )
    }

    suspend fun apiPreapprovalNew(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        unifiedCheckoutApiService.mobileMoneyPreapprovalNew(
            salesId = salesId,
            req = req
        )
    }

    suspend fun getFees(
        salesId: String,
        req: GetFeesReq,
    ): ResultWrapper2<CheckoutFee> = makeRequestToApi {
        unifiedCheckoutApiService.getFees(salesId, req.channel, req.amount)
    }

    suspend fun getTransactionStatusDirectDebit(
        salesId: String,
        clientReference: String,
    ): ResultWrapper2<TransactionStatusInfo> = makeRequestToApi {
        unifiedCheckoutApiService.getTransactionStatus(salesId, clientReference)
    }

    suspend fun verifyOtp(
        salesId: String, req: OtpReq
    ): ResultWrapper2<OtpResponse> = makeRequestToApi {
        unifiedCheckoutApiService.verifyOtp(salesId, req)
    }

    suspend fun getCustomerWallets(
        salesId: String?, phoneNumber: String?
    ): ResultWrapper<List<WalletResponse>> = makeRequestToApi {
        unifiedCheckoutApiService.getWallets(salesId, phoneNumber)
    }

    suspend fun getBusinessPaymentChannels(salesId: String): ResultWrapper<PaymentChannelResponse> =
        makeRequestToApi {
            unifiedCheckoutApiService.getBusinessChannels(salesId)
        }

    suspend fun getGhanaCardDetails(
        salesId: String?, phoneNumber: String?
    ): ResultWrapper2<GhanaCardResponse> = makeRequestToApi {
        unifiedCheckoutApiService.getGhanaCardDetails(salesId, phoneNumber)
    }

    suspend fun addGhanaCard(
        salesId: String?, phoneNumber: String?, cardId: String?
    ): ResultWrapper2<GhanaCardResponse> = makeRequestToApi {
        unifiedCheckoutApiService.addGhanaCard(salesId, phoneNumber, cardId)
    }

    suspend fun ghanaCardConfirm(
        salesId: String?, phoneNumber: String?
    ): ApiResult<Any> = makeRequestToApi {
        unifiedCheckoutApiService.ghanaCardConfirm(salesId, phoneNumber)
    }

    suspend fun ghanaCardConfirm2(
        salesId: String?,
        req: CardReq
    ): ApiResult<Any> = makeRequestToApi {
        unifiedCheckoutApiService.ghanaCardConfirm2(salesId, req)
    }

    suspend fun addWallet(
        salesId: String?,
        req: UserWalletReq
    ): ResultWrapper2<UserWalletResponse> = makeRequestToApi {
        unifiedCheckoutApiService.addWallet(
            salesId,
            req
        )
    }

    // Db
    fun getPaymentChannels(): List<PaymentChannel> {
        return checkoutPrefManager.allowedPaymentChannels ?: emptyList()
    }

    fun savePaymentChannels(channels: List<PaymentChannel>) {
        checkoutPrefManager.allowedPaymentChannels = channels
    }

    fun saveMandateId(id: String?) {
        checkoutPrefManager.mandateId  = id
    }

    fun getMandateId(): String? {
        return checkoutPrefManager.mandateId
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

    fun saveWallet(vararg wallet: HubtelWallet) {
        CoroutineScope(Dispatchers.IO).launch {
            database.hubtelDao().insert(*wallet)
        }
    }

    suspend fun savedMomoWallets(): List<HubtelWallet> {
        return withContext(Dispatchers.IO) {
            database.hubtelDao().getAllWallets()
        }
    }

}
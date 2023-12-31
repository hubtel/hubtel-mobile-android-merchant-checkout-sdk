package com.hubtel.merchant.checkout.sdk.platform.data.source.repository

import com.hubtel.merchant.checkout.sdk.network.ResultWrapper2
import com.hubtel.merchant.checkout.sdk.network.repository.Repository
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.CheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
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

    suspend fun apiReceiveMobileMoney(
        salesId: String,
        req: MobileMoneyCheckoutReq,
    ): ResultWrapper2<CheckoutInfo> = makeRequestToApi {
        checkoutApiService.receiveMobileMoney(salesId, req)
    }

    suspend fun getFees(
        salesId: String,
        req: GetFeesReq,
    ): ResultWrapper2<List<CheckoutFee>> = makeRequestToApi {
        checkoutApiService.getFees(salesId, req)
    }


    suspend fun getTransactionStatus(
        salesId: String,
        clientReference: String,
    ): ResultWrapper2<TransactionStatusInfo> = makeRequestToApi {
        checkoutApiService.getTransactionStatus(salesId, clientReference)
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
package com.hubtel.sdk.checkout.platform.data.source.repository

import com.hubtel.core_network.ResultWrapper2
import com.hubtel.core_network.model.requests.UserCardDetailsReq
import com.hubtel.core_network.model.response.CheckoutInfo
import com.hubtel.core_network.model.response.ThreeDSSetupInfo
import com.hubtel.core_network.repository.Repository
import com.hubtel.core_network.service.CheckoutApiService
import com.hubtel.sdk.checkout.platform.data.source.db.CheckoutDB

internal class CheckoutRepository(
    private val database: CheckoutDB,
    private val checkoutApiService: CheckoutApiService,
) : Repository() {

    suspend fun apiSetup3DS(
        salesId: String?,
        req: UserCardDetailsReq
    ): ResultWrapper2<ThreeDSSetupInfo> {
        return makeRequestToApi {
            checkoutApiService.setup3DS(salesId, req)
        }
    }

    suspend fun apiEnroll3DS(
        salesId: String?,
        transactionId: String?
    ): ResultWrapper2<CheckoutInfo> {
        return makeRequestToApi {
            checkoutApiService.enroll3DS(salesId, transactionId)
        }
    }
}
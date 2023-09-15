package com.hubtel.merchant.checkout.sdk.platform.data.source.api


import com.hubtel.merchant.checkout.sdk.network.createRetrofitService
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse2
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface CheckoutApiService {

    // 'https://checkout.hubtel.com/api/v1/merchant/11684/unifiedcheckout/statuscheck/YJKOOKKKJJJHH'

    @POST("/v2/merchantaccount/merchants/{salesId}/merchantcardnotpresent/setup-payerauth")
    suspend fun setup3DS(
        @Path("salesId") salesId: String,
        @Body req: ThreeDSSetupReq,
    ): DataResponse2<ThreeDSSetupInfo>

    @GET("/v2/merchantaccount/merchants/{salesId}/merchantcardnotpresent/enroll-payerauth/{transactionId}")
    suspend fun enroll3DS(
        @Path("salesId") salesId: String,
        @Path("transactionId") transactionId: String,
    ): DataResponse2<CheckoutInfo>

    @POST("/v2/merchantaccount/merchants/{salesId}/receive/mobilemoney")
    suspend fun receiveMobileMoney(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    @POST("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/receive/mobilemoney/directdebit")
    suspend fun receiveMobileMoneyDirectDebit(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>


    @POST("/v2/merchantaccount/merchants/{salesId}/fees")
    suspend fun getFees(
        @Path("salesId") salesId: String,
        @Body req: GetFeesReq,
    ): DataResponse2<List<CheckoutFee>>

    @POST("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/feecalculation")
    suspend fun getFeesDirectDebitCall(
        @Path("salesId") salesId: String,
        @Body req: GetFeesReq,
    ): DataResponse2<List<CheckoutFee>>

    @GET("/v2/merchantaccount/merchants/{salesId}/statuscheck/{clientReference}")
    suspend fun getTransactionStatus(
        @Path("salesId") salesId: String,
        @Path("clientReference") clientReference: String,
    ): DataResponse2<TransactionStatusInfo>

    @GET("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/statuscheck/{clientReference}")
    suspend fun getTransactionStatusDirectDebit(
        @Path("salesId") salesId: String,
        @Path("clientReference") clientReference: String,
    ): DataResponse2<TransactionStatusInfo>

    @GET("/v2/merchantaccount/merchants/{salesId}/paymentchannels")
    suspend fun getBusinessPaymentChannels(
        @Path("salesId") salesId: String
    ): DataResponse2<List<String>>

    companion object {
        operator fun invoke(apiKey: String): CheckoutApiService {
            return createRetrofitService(
                baseUrl = "https://merchantcard-proxy.hubtel.com",
                apiKey = apiKey
            )
        }
    }

}
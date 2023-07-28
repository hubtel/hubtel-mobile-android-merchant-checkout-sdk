package com.hubtel.merchant.checkout.sdk.platform.data.source.api

internal class CheckoutApiService2(apiKey: String)
//    : ApiService(
//    "https://merchantcard-proxy.hubtel.com"
//) {

//    init {
//        setAuthorization(apiKey, AuthorizationType.BASIC)
//    }


//    @GET("/v2/merchantaccount/merchants/{salesId}/statuscheck/{clientReference}")
//    suspend fun getTransactionStatus(
//        @Path("salesId") salesId: String,
//        @Path("clientReference") clientReference: String,
//    ): DataResponse2<TransactionStatusInfo>

//    suspend fun getTransactionStatus(
//        salesId: String,
//        clientReference: String,
//    ) : ApiResult<DataResponse2<TransactionStatusInfo>> {
//        return get {
//            url = "/v2/merchantaccount/merchants/${salesId}/statuscheck/${clientReference}"
//        }
//    }
//}
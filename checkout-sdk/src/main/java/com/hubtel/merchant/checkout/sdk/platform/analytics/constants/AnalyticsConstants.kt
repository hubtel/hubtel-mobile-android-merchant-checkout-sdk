package com.hubtel.merchant.checkout.sdk.platform.analytics.constants

internal object AnalyticsConstants {

    object Param {

        // section
        const val SECTION = "Section"
        const val SECTION_ID = "Section Id"
        const val SECTION_NAME = "Section Name"

        // view
        const val VIEW_NAME = "View Name"
        const val VIEW_ID = "View Id"
        const val VIEW_SHORT_NAME = "View ShortName"
        const val VIEW_ITEM_NAME = "View Item Name"
        const val VIEW_ITEM_ID = "View Item Id"

        // tap
        const val TAP_ID = "Tap Id"
        const val TAP_NAME = "Tap Name"
        const val TAP_SHORT_NAME = "Tap ShortName"

        // search
        const val QUERY = "Query"
        const val SEARCH_ID = "Search Id"
        const val SEARCH_NAME = "Search Name"
        const val SEARCH_SHORT_NAME = "Search ShortName"
        const val SEARCH_SELECTED_RESULT = "Search Selected Result"
        const val SEARCH_RESULT_FOUND = "Search Result Found"
        const val SEARCH_SELECTED_RESULT_ITEM_ID = "Search Selected Result Item Id"

        // share
        const val SHARE_ID = "Share Id"
        const val SHARE_NAME = "Share Name"
        const val SHARE_SHORT_NAME = "Share ShortName"

        // report
        const val REPORT_ID = "Report Id"
        const val REPORT_NAME = "Report Name"
        const val REPORT_SHORT_NAME = "Report ShortName"

        // purchase
        const val PURCHASE_AMOUNT = "Purchase Amount"
        const val PURCHASE_ERROR_MESSAGE = "Purchase Error Message"
        const val PURCHASE_PAYMENT_CHANNEL = "Purchase Payment Channel"
        const val PURCHASE_PAYMENT_TYPE = "Purchase Payment Type"
        const val PURCHASE_ORDER_ITEMS = "Purchase Order Items"
        const val PURCHASE_ORDER_ITEM_NAMES = "Purchase Order Item Names"
        const val PURCHASE_ORDER_PROVIDERS = "Purchase Order Providers"
        const val QUANTITY = "Quantity"
        const val AMOUNT = "Amount"
        const val ITEM_NAME = "Name"
        const val ID = "Id"
        const val PROVIDER = "Provider"

        // api
        const val API_BASE_URL = "API Base Url"
        const val API_URL = "API URL"
        const val API_STATUS = "API Status"
        const val API_STATUS_CODE = "API Status Code"
        const val API_ERROR_MESSAGE = "API Error Message"
        const val API_RESPONSE_TIME_IN_SECONDS = "API Response Time In Seconds"
        const val API_RESPONSE_SIZE_IN_KILOBYTES = "API Response Size In Kilobytes"
        const val API_RESPONSE_SIZE_IN_MEGA_BYTES = "API Response Size In Megabytes"

        const val PAGE_NAME = "Page Name"
        const val UI_TYPE = "UI Type"

        // people
        const val NAME = "\$name"
        const val ZONE = "Zone"

        const val COUNT_ORDER_PAYMENT_SUCCESSFUL = "Count Order Payment Successful"
        const val TOTAL_ORDER_PAYMENT_SUCCESS = "Total Order Payment Successful"

        const val COUNT_ORDER_PAYMENT_FAILED = "Count Order Payment Failed"
        const val TOTAL_ORDER_PAYMENT_FAILED = "Total Order Payment Failed"

        // cart
        const val CART_ITEMS = "Cart Items"
        const val CART_AMOUNT = "Cart Amount"
        const val CART_ITEM_NAMES = "Cart Item Names"
        const val CART_ITEM_PROVIDERS = "Cart Item Providers"

        // order-validation
        const val MESSAGE = "message"
    }

    object EventStoreParam {
        const val API_RESPONSE_SIZE_IN_MEGA_BYTES = "apiResponseSizeInMegaBytes"
        const val API_RESPONSE_SIZE_IN_KILOBYTES = "apiResponseSizeInKilobytes"
        const val API_RESPONSE_TIME_IN_SECONDS = "apiResponseTimeInSeconds"
        const val API_ERROR_MESSAGE = "apiErrorMessage"
        const val API_STATUS_CODE = "apiStatusCode"
        const val API_STATUS = "apiStatus"
        const val API_URL = "apiUrl"
        const val API_BASE_URL = "apiBaseUrl"

        const val CUSTOMER = "customer"
        const val CUSTOMER_PHONE_NUMBER = "customerPhoneNumber"
        const val LOCATION = "location"
        const val ZONE = "zone"
        const val LATITUDE = "lat"
        const val LONGITUDE = "lon"
        const val STATION = "station"
        const val SESSION_ID = "sessionId"

        const val ACTION: String = "action"
        const val ACTION_NAME: String = "actionName"

        const val PAGE: String = "page"
        const val APP_VERSION: String = "appVersion"
        const val OS: String = "os"
        const val TIME: String = "time"
        const val APP_NAME: String = "appName"
        const val APP_BUILD_NUMBER: String = "appBuildNumber"

        const val SECTION_NAME: String = "sectionName"
        const val UI_TYPE = "uiType"
        const val PAGE_NAME = "pageName"

        // tap
        const val TAP_ID = "tapId"
        const val TAP_NAME = "tapName"
        const val TAP_SHORT_NAME = "tapShortName"

        // view
        const val VIEW_NAME = "viewName"
        const val VIEW_ID = "viewId"
        const val VIEW_SHORT_NAME = "viewShortName"
        const val VIEW_ITEM_NAME = "viewItemName"
        const val VIEW_ITEM_ID = "viewItemId"

        // purchase
        const val PURCHASE_AMOUNT = "purchaseAmount"
        const val PURCHASE_ERROR_MESSAGE = "purchaseErrorMessage"
        const val PURCHASE_PAYMENT_CHANNEL = "purchasePaymentChannel"
        const val PURCHASE_PAYMENT_TYPE = "purchasePaymentType"
        const val PURCHASE_ORDER_ITEMS = "purchaseOrderItems"
        const val PURCHASE_ORDER_ITEM_NAMES = "purchaseOrderItemNames"
        const val PURCHASE_ORDER_PROVIDERS = "purchaseOrderProviders"
        const val SECTION = "section"
        const val QUANTITY = "quantity"
        const val AMOUNT = "amount"
        const val ITEM_NAME = "name"
        const val PROVIDER = "provider"
        const val ID = "id"

        // search
        const val SEARCH_QUERY = "searchQuery"
        const val SEARCH_ID = "searchId"
        const val SEARCH_NAME = "searchName"
        const val SEARCH_SHORT_NAME = "searchShortName"
        const val SEARCH_SELECTED_RESULT = "searchSelectedResult"
        const val SEARCH_RESULT_FOUND = "searchResultFound"
        const val SEARCH_SELECTED_RESULT_ITEM_ID = "searchSelectedResultItemId"

        // cart
        const val CART_ITEMS = "cartItems"
        const val CART_AMOUNT = "cartAmount"
        const val CART_ITEM_NAMES = "cartItemNames"
        const val CART_ITEM_PROVIDERS = "cartItemProviders"
    }
}

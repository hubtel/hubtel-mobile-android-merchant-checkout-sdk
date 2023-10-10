package com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections

import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEventEnum

enum class CheckoutEvent(
    internal val rawValue: String
): AnalyticsEventEnum {
    //  view
    CheckoutPayViewPagePay("checkout__pay__view__page_pay"),

    CheckoutDsBrowserViewPageDsBrowser("checkout__3ds_browser__view__page_3ds_browser"),

    CheckoutDsBrowserViewDialogDsError("checkout__3ds_browser__view__dialog_3ds_error"),

    CheckoutPayViewDialogOrderCreatedSuccessfully("checkout__pay__view__dialog_order_created_successfully"),

    CheckoutPayViewDialogOrderFailed("checkout__pay__view__dialog_order_failed"),

    CheckoutCheckStatusViewPageCheckStatus("checkout__check_status__view__page_check_status"),

    CheckoutCheckStatusViewPageCheckAgain("checkout__check_status__view__page_check_again"),

    CheckoutPaymentFailedViewPagePaymentFailed("checkout__payment_failed__view__page_payment_failed"),

    CheckoutPaymentSuccessfulViewPagePaymentSuccessful("checkout__payment_successful__view__page_payment_successful"),

    //  tap
    CheckoutPayTapMobileMoney("checkout__pay__tap__mobile_money"),

    CheckoutPayTapBankCard("checkout__pay__tap__bank_card"),
    CheckoutPayTapOtherPayment("checkout__pay__tap__other_payment"),
    CheckoutPayTapPayIn4("checkout__pay__tap__pay_in_four"),

    CheckoutPayTapUseNewCard("checkout__pay__tap__use_new_card"),

    CheckoutPayTapUseSavedCard("checkout__pay__tap__use_saved_card"),

    CheckoutPayTapHubtelBalance("checkout__pay__tap__hubtel_balance"),

    CheckoutPayTapButtonPay("checkout__pay__tap__button_pay"),

    CheckoutPayTapClose("checkout__pay__tap__close"),

    CheckoutCheckStatusTapIHavePaid("checkout__check_status__tap__i_have_paid"),

    CheckoutCheckStatusTapCancel("checkout__check_status__tap__cancel"),

    CheckoutCheckStatusTapCheckAgain("checkout__check_status__tap__check_again"),

    CheckoutCheckStatusTapChangeWallet("checkout__check_status__tap__change_wallet"),

    CheckoutPaymentSuccessfulTapButtonDone("checkout__payment_successful__tap__button_done");

    override val value: String
        get() = this.rawValue

}

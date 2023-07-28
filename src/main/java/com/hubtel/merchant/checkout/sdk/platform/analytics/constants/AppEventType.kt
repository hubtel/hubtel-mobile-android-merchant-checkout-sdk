package com.hubtel.core_analytics.constants

internal enum class AppEventType(internal val rawValue: String) {
    Login("Login"),
    RequestOtp("Request OTP"),
    OtpValidationFailed("OTP Validation Failed"),
    ViewSection("View Section"),
    BeginPurchase("Begin Purchase"),
    Purchase("Purchase"),
    PurchaseFailed("Purchase Failed"),
    View("View"),
    ViewItem("View Item"),
    Search("Search"),
    Tap("Tap"),
    Report("Report"),
    Share("Share"),
    ApiRequest("API Request"),
    AddToCart("Add To Cart"),
    RemoveFromCart("Remove From Cart"),
    ClearCart("Clear Cart"),
    ViewCart("View Cart"),
    Other("Other"),
}
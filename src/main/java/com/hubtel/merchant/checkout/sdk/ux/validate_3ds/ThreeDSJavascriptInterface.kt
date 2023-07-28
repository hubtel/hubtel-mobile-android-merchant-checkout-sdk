package com.hubtel.feature_checkout.ui.validate_3ds

import android.webkit.JavascriptInterface
import android.webkit.WebView
import timber.log.Timber


/**
* This class is used to create a javascript object which can be accessed from
 * [WebView.addJavascriptInterface]. The functions exposed in this class are called
 * from the javascript code.
 * @param on3DSEnroll callback function invoked when the user 3DS enrollment should begin.
 * @param onSuccess callback function invoked when user 3DS authentication is completed.
 * @param onFailure callback function invoked when user fails or doesn't complete the
 * 3DS challenge presented.
 * */
internal class ThreeDSJavascriptInterface(
    private val on3DSEnroll: () -> Unit,
    private val onSuccess: () -> Unit,
    private val onFailure: () -> Unit,
) {


    @JavascriptInterface
    fun onDataCollectionCompleted() {
        Timber.i("3DS: Enroll: Start")
        on3DSEnroll()
    }


    @JavascriptInterface
    fun onValidationComplete() {
        Timber.i("3DS: Enroll: Validation Successful")
        onSuccess()
    }


    @JavascriptInterface
    fun onValidationError() {
        Timber.i("3DS: Enroll: Validation Error")
        onFailure()
    }

    companion object {
        const val JS_NAME = "ThreeDSJsInterface"
    }
}

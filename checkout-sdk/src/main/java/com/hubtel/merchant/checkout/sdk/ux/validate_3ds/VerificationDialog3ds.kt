package com.hubtel.merchant.checkout.sdk.ux.validate_3ds

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun VerificationDialog3ds(
    deviceInfoHtml: String,
    onDeviceCollectionComplete: () -> Unit
) {

    HBScaffold(
        topBar = {
            Column {
                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(stringResource(R.string.checkout_additional_verification_title))
                        pop()
                        append("\n")
                        append(stringResource(R.string.checkout_additional_verification_subtitle))
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Dimens.paddingDefault)
                )
            }
        },
        backgroundColor = HubtelTheme.colors.uiBackground2,
    ) {
        DeviceCollectionWebView(
            html = deviceInfoHtml,
            onDeviceCollectionComplete = onDeviceCollectionComplete
        )
    }
}

private const val TAG = "VerificationDialog3ds"


@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun DeviceCollectionWebView(
    html: String,
    onDeviceCollectionComplete: () -> Unit
) {
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(0, 0)

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            CookieManager.getInstance().setAcceptCookie(true)

            addJavascriptInterface(
                CheckoutJsInterface { data ->
                    if (data == CheckoutJsInterface.ENROLLMENT) {
                        onDeviceCollectionComplete.invoke()
                    }
                },
                CheckoutJsInterface.JS_NAME,
            )

            val jsInjectedHtml = html.replace(
                "CONTROL_RETURN_IDENTIFIER",
                "${CheckoutJsInterface.JS_NAME}.postMessage('${CheckoutJsInterface.ENROLLMENT}')"
            )
            // getHtml from 3ds result and replace the parts
            loadDataWithBaseURL(
                "https://localhost/", jsInjectedHtml,
                "text/html", "UTF-8", null,
            )
        }
    })
}


internal class CheckoutJsInterface(
    private val onSomeCheckoutAction: (result: Any?) -> Unit
) {

    @JavascriptInterface
    fun postMessage(data: String) {
        onSomeCheckoutAction(data)
    }

    companion object {
        const val JS_NAME = "LoginJavascriptInterface"
        const val ENROLLMENT = "Enrollment"
    }
}
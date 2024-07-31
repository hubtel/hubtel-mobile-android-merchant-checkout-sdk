package com.hubtel.merchant.checkout.sdk.ux.validate_3ds

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.components.HBProgressDialog
import com.hubtel.merchant.checkout.sdk.ux.pay.order.ThreeDSSetupState
import timber.log.Timber

//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//internal fun Collect3dsDeviceInfoDialog(
//    setupState: ThreeDSSetupState,
//    onCollectionComplete: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//
//    Box(modifier.fillMaxSize()) {
//        AndroidView(
//            factory = { ctx ->
//                WebView(ctx).apply {
//                    this.layoutParams = ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                    )
//
//                    settings.javaScriptEnabled = true
//                    settings.domStorageEnabled = true
//                    CookieManager.getInstance().setAcceptCookie(true)
//
//                    addJavascriptInterface(
//                        CollectDeviceInfoJavascriptInterface(onCollectionComplete),
//                        CollectDeviceInfoJavascriptInterface.JS_NAME,
//                    )
//
//                    webViewClient = WebViewClient()
//                    webChromeClient = WebChromeClient()
//                }
//            },
//            update = {
//                it.loadDataWithBaseURL(
//                    "https://localhost/", pageHtml,
//                    "text/html", "UTF-8", null,
//                )
//            },
//            modifier = Modifier
//                .size(0.dp, 0.dp)
//        )
//
//        HBProgressDialog(
//            message = "${stringResource(R.string.checkout_please_wait)}..."
//        )
//    }
//}
//
//internal class CollectDeviceInfoJavascriptInterface(
//    private val onComplete: () -> Unit
//) {
//
//    @JavascriptInterface
//    fun onDataCollectionCompleted() {
//        Timber.tag(JS_NAME).i("Device Info Collection Complete")
//        onComplete()
//    }
//
//    companion object {
//        const val JS_NAME = "CollectDeviceInfoJavascriptInterface"
//    }
//}
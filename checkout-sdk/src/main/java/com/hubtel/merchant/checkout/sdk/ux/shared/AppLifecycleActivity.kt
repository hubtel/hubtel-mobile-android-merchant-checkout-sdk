package com.hubtel.merchant.checkout.sdk.ux.shared

import android.content.Context
import androidx.fragment.app.FragmentActivity

abstract class AppLifecycleActivity : FragmentActivity() {

    private val lifecycleCallback: AppLifecycleCallback?
        get() = (application as? AppLifecycleCallback);

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (!isAppInBackground) {
            lifecycleCallback?.didEnterBackground(this)
            isAppInBackground = true
        }
    }

    override fun onResume() {
        super.onResume()

        // if app entered background, then we're resuming from background
        if (isAppInBackground) {
            lifecycleCallback?.didEnteredForeground(this)
            isAppInBackground = false
        }
    }

    companion object {
        // default to an initial value of true because from "background"
        private var isAppInBackground: Boolean = true
    }
}

interface AppLifecycleCallback {

    fun didEnteredForeground(context: Context)

    fun didEnterBackground(context: Context)

}
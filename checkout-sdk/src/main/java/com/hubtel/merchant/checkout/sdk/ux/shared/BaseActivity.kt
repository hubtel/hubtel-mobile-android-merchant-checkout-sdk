package com.hubtel.merchant.checkout.sdk.ux.shared

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.hubtel.merchant.checkout.sdk.ux.utils.ProvideAppNavigator
import com.hubtel.merchant.checkout.sdk.ux.utils.ProvideBaseActivity
import com.hubtel.merchant.checkout.sdk.ux.utils.ProvideDeeplinkNavigator
import com.hubtel.merchant.checkout.sdk.ux.navigation.DeeplinkNavigator
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

abstract class BaseActivity : AppLifecycleActivity() {

    @Composable
    open fun RootContent() {
        Text("Override fun RootContent() in BaseActivity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HubtelTheme {
                ProvideBaseActivity(this) {
                    ProvideAppNavigator(provideAppNavigator()) {
                        ProvideDeeplinkNavigator(provideDeeplinkNavigator()) {
                            RootContent()
                        }
                    }
                }
            }
        }
    }

    open fun provideAppNavigator(): BaseAppNavigator? = null

    open fun provideDeeplinkNavigator(): DeeplinkNavigator? = null

    val hasDeepLink: Boolean get() = intent.data != null

    val deepLinkUri: Uri? get() = intent.data

}
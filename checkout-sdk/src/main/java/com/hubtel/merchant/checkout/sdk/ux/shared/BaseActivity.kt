package com.hubtel.merchant.checkout.sdk.ux.shared

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
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

        // Handle window insets ourselves so Compose receives the animated IME
        // insets and pushes content above the soft keyboard (iOS-like behavior),
        // instead of relying on the legacy adjustResize window resize.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContent {
            HubtelTheme {
                val darkTheme = isSystemInDarkTheme()
                SideEffect {
                    val controller =
                        WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !darkTheme
                    controller.isAppearanceLightNavigationBars = !darkTheme
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.systemBars.union(WindowInsets.ime)
                        )
                ) {
                    ProvideBaseActivity(this@BaseActivity) {
                        ProvideAppNavigator(provideAppNavigator()) {
                            ProvideDeeplinkNavigator(provideDeeplinkNavigator()) {
                                RootContent()
                            }
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

# Keyboard IME "Push Content Up" Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the soft keyboard push checkout content up (iOS-like) instead of covering input fields, fixed once at the root so all SDK input screens benefit.

**Architecture:** Opt into edge-to-edge in `BaseActivity` (`WindowCompat.setDecorFitsSystemWindows(window, false)`) so Compose receives animated IME insets, then wrap all content in a single root `Box` that applies `windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.ime))`. No per-screen changes — each screen's existing `verticalScroll` + Material `TextField` bring-into-view handles the rest once the viewport shrinks.

**Tech Stack:** Kotlin, Jetpack Compose 1.2.1 (Material 2), Voyager navigation, `androidx.core` `WindowCompat`.

## Global Constraints

- Compose version is **1.2.1** — do NOT use `safeDrawingPadding()` / `WindowInsets.safeDrawing` (added in 1.4.0). Use only: `windowInsetsPadding`, `WindowInsets.systemBars`, `WindowInsets.ime`, `WindowInsets.union`.
- `androidx.activity:activity-compose` is **1.6.0** — `enableEdgeToEdge()` (activity 1.8+) is unavailable; call `WindowCompat.setDecorFitsSystemWindows` directly.
- Keep `android:windowSoftInputMode="adjustResize"` in the manifest — with `decorFitsSystemWindows(false)` it is what delivers animated `WindowInsets.ime`. Do NOT remove it.
- The only code change is in `BaseActivity`; do NOT modify individual screens.
- IME / window-inset behavior cannot be meaningfully unit-tested; the automated gate is a clean compile, and correctness is confirmed by the manual device matrix in Task 2.

---

### Task 1: Root IME-aware inset handling in BaseActivity

**Files:**
- Modify: `checkout-sdk/build.gradle` (dependencies block, near line 90)
- Modify: `checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/shared/BaseActivity.kt`

**Interfaces:**
- Consumes: existing `RootContent()`, `ProvideBaseActivity`, `ProvideAppNavigator`, `ProvideDeeplinkNavigator`, `HubtelTheme` (unchanged signatures).
- Produces: no new public API. Behavioral change only — every `BaseActivity` subclass (only `CheckoutActivity` in the SDK) now draws edge-to-edge with system-bar + IME insets applied at the root.

- [ ] **Step 1: Add the explicit `core-ktx` dependency**

In `checkout-sdk/build.gradle`, add this line immediately after the `androidx.appcompat` line (currently line 90):

```gradle
    api 'androidx.appcompat:appcompat:1.6.1'
    api 'androidx.core:core-ktx:1.9.0'
```

(`1.9.0` matches the version appcompat 1.6.1 already pulls in, so it introduces no upgrade.)

- [ ] **Step 2: Replace BaseActivity.kt with the inset-aware version**

Replace the entire contents of `checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/shared/BaseActivity.kt` with:

```kotlin
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
```

Key points compared to the original:
- `this` inside the `Box` lambda would resolve to `BoxScope`, so `ProvideBaseActivity` now receives `this@BaseActivity`.
- `android.graphics.Color` is imported (not Compose `Color`); BaseActivity does not use Compose `Color`, so there is no clash.
- The `union(systemBars, ime)` padding takes the max per edge, so the bottom inset never double-counts navigation bar + keyboard.

- [ ] **Step 3: Compile the SDK module to verify it builds**

Run: `./gradlew :checkout-sdk:assembleDebug`
Expected: `BUILD SUCCESSFUL`. No unresolved-reference errors for `windowInsetsPadding`, `union`, `WindowCompat`, `systemBars`, or `ime`.

- [ ] **Step 4: Commit**

```bash
git add checkout-sdk/build.gradle checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/shared/BaseActivity.kt
git commit -m "fix: push checkout content above soft keyboard via root IME insets"
```

---

### Task 2: Manual device-matrix verification

**Files:** none (verification only).

This is the reviewer gate that actually confirms the fix. There is no automated
substitute for on-device IME behavior.

- [ ] **Step 1: Build and install the demo app**

Run: `./gradlew :app:installDebug`
Expected: `BUILD SUCCESSFUL`, app installed on the connected device/emulator.

- [ ] **Step 2: Verify the checkout home screen (PayOrderScreen)**

Launch checkout, expand the Mobile Money option, tap the phone-number field.
Expected:
- The field animates upward and stays fully visible above the keyboard.
- The "Pay" button rides up and sits directly above the keyboard.
- The animation is smooth (no jump/flicker).

Repeat with the Bank Card fields (card number, expiry, CVV).

- [ ] **Step 3: Verify the other input screens**

For each: tap each input field and confirm it stays visible above the keyboard:
- Add Wallet (`AddWalletScreen`)
- Add Mandate (`AddMandateScreen`)
- Ghana Card verification (`GhCardVerificationScreen`)
- OTP entry (`OtpVerifyScreen`)

- [ ] **Step 4: Verify no system-bar visual regression (keyboard closed)**

On the checkout home screen with no keyboard open, confirm:
- The top app bar is not drawn underneath / overlapping the status bar.
- Status-bar and navigation-bar icons are legible against the background.
- Content does not sit under the navigation bar.

- [ ] **Step 5: Verify across the device matrix**

Repeat Steps 2–4 across:
- Navigation: gesture navigation and 3-button navigation.
- Keyboards: Gboard and one OEM keyboard (e.g. Samsung/SwiftKey).
- API levels: 24, 29, and 33+.

Note: on API < 30 the push-up may be non-animated (foundation 1.2.1 compat
fallback) but the field must still end up visible above the keyboard.

- [ ] **Step 6: Record results**

Document any device/keyboard where the field is still covered or a visual
regression appears. If all pass, the fix is complete.

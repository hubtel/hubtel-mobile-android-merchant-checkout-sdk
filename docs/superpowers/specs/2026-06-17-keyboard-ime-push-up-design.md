# Keyboard IME "Push Content Up" — Design

**Date:** 2026-06-17
**Branch:** keyboard-fix
**Status:** Approved (design)

## Problem

Users of the checkout SDK report that on the checkout home screen
(`PayOrderScreen`), when a merchant taps an input field (e.g. the mobile-money
phone number), the soft keyboard covers the input field instead of pushing the
content up — the desired iOS-like behavior.

## Root cause

- `CheckoutActivity` declares `android:windowSoftInputMode="adjustResize"` in the
  SDK manifest.
- The screen uses a Material 2 `Scaffold` (`HBScaffold`) with the Pay button in
  the `bottomBar` slot and content in a `Column` with
  `.padding(paddingValues).verticalScroll(...)` (`PayOrderScreen.kt:323-330`).
- The focused field lives inside `ExpandableMomoOption` (and equivalents on other
  screens).
- **There is no window-inset handling anywhere in the SDK** — no `imePadding()`,
  no `WindowInsets`, no `WindowCompat.setDecorFitsSystemWindows(...)`. The SDK
  relies entirely on the legacy `adjustResize` behavior.

`adjustResize` is unreliable in pure-Compose Material 2 apps and across OEM
keyboards / gesture navigation. When it does not engage, the window never
shrinks, so the scrollable `Column` never learns the keyboard is present, the
focused field's automatic bring-into-view has nowhere to scroll, and the
keyboard sits on top of the field.

The same latent bug affects every input screen in the SDK: `PayOrderScreen`,
`AddWalletScreen`, `AddMandateScreen`, `GhCardVerificationScreen`,
`OtpVerifyScreen`.

## Scope

**Global fix** — one root-level change that covers all input screens, rather than
patching each screen.

## Approach

Edge-to-edge + explicit safe-inset handling at the root, applied once in
`BaseActivity`. This is deterministic across OEMs and yields the smooth, animated
push-up behavior. Individual screens are not modified.

### Compose version constraint

The project is on Compose **1.2.1**. `safeDrawingPadding()` / `WindowInsets.safeDrawing`
do **not** exist until 1.4.0, so we must not use them. The following APIs are
available at 1.2.0+ and are what we use:

- `Modifier.windowInsetsPadding(...)`
- `WindowInsets.systemBars`, `WindowInsets.ime`
- `WindowInsets.union(...)`
- `WindowCompat.setDecorFitsSystemWindows(window, false)` (from `androidx.core`)

`androidx.activity:activity-compose` is at 1.6.0, so `enableEdgeToEdge()`
(activity 1.8+) is not available; we call `WindowCompat.setDecorFitsSystemWindows`
directly.

### `adjustResize` stays

`adjustResize` remains in the manifest. With `decorFitsSystemWindows(false)`, it is
what allows Compose to receive the animated `WindowInsets.ime`. It is **not**
redundant.

## Changes

### 1. `BaseActivity.onCreate` (the only code change)

Before `setContent`:

1. `WindowCompat.setDecorFitsSystemWindows(window, false)` — opt into handling
   insets ourselves so Compose receives animated `WindowInsets.ime`.
2. Set status/navigation bar colors to transparent (content now draws
   edge-to-edge) and set bar icon appearance for contrast, to preserve the
   current visual appearance.

Wrap the `RootContent()` call in:

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.ime))
) {
    RootContent()
}
```

Using `systemBars.union(ime)` (rather than separate `navigationBarsPadding()` +
`imePadding()`) takes the max per edge, so the bottom inset never double-counts
the navigation bar plus the keyboard.

### 2. No per-screen changes

Every screen uses `HBScaffold` + a `verticalScroll` content `Column`. Once the
root viewport shrinks by the IME height, each Material `TextField`'s built-in
bring-into-view scrolls the focused field into the now-smaller visible area, and
the Pay button (in `bottomBar`) rides up above the keyboard — automatically, for
all five input screens.

### 3. Dependency

`WindowCompat` comes from `androidx.core` and is available transitively. Add an
explicit `androidx.core:core-ktx` entry to `checkout-sdk/build.gradle` to be safe.

## Data flow (field tapped)

IME animates in → `WindowInsets.ime` grows → root bottom padding grows → Scaffold
height shrinks → `bottomBar` animates upward and content viewport shrinks →
focused `TextField` auto-scrolls into view. Smooth, iOS-like.

## Risks / edge cases

- **Visual regression on system bars** (now edge-to-edge): mitigated by
  transparent bars + content background showing through + icon-appearance
  setting. This is the primary thing to verify.
- **Dialogs** (`CheckoutMessageDialog`, `HBProgressDialog`) are separate windows —
  unaffected. **Bottom sheet** (`HBModalBottomSheetLayout`) is in-composition and
  is covered by the root padding.
- **Pre-API 30 IME animation**: foundation 1.2.1 falls back via the compat shim;
  worst case it is non-animated but still pushes up correctly.
- **Other activities**: `CheckoutActivity` is the only `BaseActivity` subclass in
  the SDK (the match in `ActivityCompositionLocal.kt` is a type reference, not a
  subclass), so the root change is correctly global within the SDK.

## Testing

Automated IME testing is unreliable, so verification is primarily a manual device
matrix:

- Navigation: gesture-nav and 3-button nav
- Keyboards: Gboard and one OEM keyboard
- API levels: 24 / 29 / 33+

For each input screen (checkout home momo + card fields, OTP, add wallet, add
mandate, GH card):

- Tap field → field stays visible
- Pay button sits above the keyboard
- Animation is smooth
- No status-bar overlap when the keyboard is closed (no visual regression)

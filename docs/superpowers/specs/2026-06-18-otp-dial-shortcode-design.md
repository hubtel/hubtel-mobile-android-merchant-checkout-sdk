# OTP "Didn't receive the code?" Dial Prompt — Design

**Date:** 2026-06-18
**Branch:** keyboard-fix
**Status:** Approved (design)

## Problem

The Verify OTP screen (`OtpVerifyScreen`) does not tell users what to do if they
never receive the SMS code. The iOS app already shows a prompt with a USSD
shortcode to view the code; Android is the only platform missing it. We want
parity.

## Goal

On the Verify OTP screen, below the OTP entry boxes, show:

> Didn't receive the code? Dial **`*713*90#`** to view it

The shortcode (`*713*90#`) is styled in the brand/primary color and is tappable;
tapping it opens the phone dialer pre-filled with the code.

## Decisions

- **Shortcode source:** static `*713*90#`. The OTP API response (`OtpResponse` /
  `OtpRequestResponse`) has no dial-code field, and iOS hardcodes it. No backend
  or API changes.
- **Interaction:** tappable. Tapping launches the dialer via `ACTION_DIAL`
  (no permission required); the user presses call manually (no auto-dial).

## Placement

`OtpVerifyScreen.kt`, in the content `Column`, directly below the OTP entry boxes
— after the existing spacer `Box(modifier = Modifier.padding(Dimens.paddingNano))`
that follows `OtpTextField(...)`, and before the error/loading dialog blocks.
This matches the iOS layout.

## Component

A new private composable, `DialToReceiveCodeText(modifier: Modifier = Modifier)`:

- Builds the sentence with `buildAnnotatedString`.
- The full sentence uses the normal text color.
- The `*713*90#` substring is colored with `HubtelTheme.colors.colorPrimary`
  (respects the merchant theme; brand teal is the default) and wrapped in a
  string annotation with tag `"dial"` and the shortcode as its value.
- The substring range is located via `indexOf` on the formatted sentence so the
  sentence stays a single translatable unit while only the code is styled.
- Rendered with `androidx.compose.foundation.text.ClickableText`.
- On click: resolve the `"dial"` annotation at the clicked offset; if present,
  fire `Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode("*713*90#")))`
  using the current context. `Uri.encode` turns `#` into `%23`.

## Strings

Add to `checkout-sdk/src/main/res/values/strings.xml`:

- `checkout_otp_dial_code` = `*713*90#`
- `checkout_otp_didnt_receive_code` = `Didn't receive the code? Dial %1$s to view it`

The composable reads `checkout_otp_dial_code`, formats
`checkout_otp_didnt_receive_code` with it, then styles/annotates the located code
substring.

Note: the apostrophe in "Didn't" must be escaped in the XML resource as `\'`.

## Error handling

Wrap `context.startActivity(...)` in a `try/catch` for `ActivityNotFoundException`
(device with no dialer app). On catch: no-op (optionally `Timber.d`). A tap must
never crash checkout.

## Scope boundaries

- No ViewModel changes.
- No API / network / model changes.
- Presentation-only addition on a single screen.

## Risks / edge cases

- **Device without a dialer** (rare, e.g. some tablets): handled by the
  try/catch — no crash, no-op.
- **USSD `#` encoding:** `Uri.encode("*713*90#")` produces `*713*90%23`, which
  `ACTION_DIAL` handles correctly.
- **Theming:** using `HubtelTheme.colors.colorPrimary` means the code color
  follows the merchant's configured primary color rather than a hardcoded teal.

## Testing

Consistent with the rest of this branch: an annotated-string tap that launches an
`Intent` has no meaningful unit test. The gate is:

1. Clean compile (`./gradlew :checkout-sdk:assembleDebug`).
2. Manual check on device: navigate to the Verify OTP screen, confirm the
   sentence appears below the OTP boxes with the shortcode in the primary color,
   and tapping the shortcode opens the dialer pre-filled with `*713*90#`.

# OTP Dial-Shortcode Prompt Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Show a tappable "Didn't receive the code? Dial *713*90# to view it" prompt below the OTP boxes on the Verify screen, matching iOS.

**Architecture:** Presentation-only. Add two string resources and one new composable (`DialToReceiveCodeText`) to `OtpVerifyScreen.kt`, placed below the OTP entry boxes. The shortcode substring is styled in the primary color and annotated; tapping it launches the dialer via `ACTION_DIAL`. No ViewModel/API changes.

**Tech Stack:** Kotlin, Jetpack Compose 1.2.1 (Material 2), `ClickableText`, `Intent.ACTION_DIAL`.

## Global Constraints

- Static shortcode value `*713*90#` — sourced from a string resource, NOT from the API (the OTP response has no dial-code field).
- Shortcode color must use `HubtelTheme.colors.colorPrimary` (theme-aware), NOT a hardcoded color.
- Use `ACTION_DIAL` (no permission), never `ACTION_CALL`. Do NOT auto-dial.
- Encode the `tel:` value with `Uri.encode(...)` so `#` becomes `%23`.
- The dialer launch must be wrapped in `try/catch (ActivityNotFoundException)` so a tap can never crash checkout.
- Presentation-only: do NOT modify `OtpVerifyViewModel`, any API model, or network code.
- No unit tests — an annotated-string tap launching an Intent has no meaningful unit test. The automated gate is a clean compile; correctness is confirmed by manual device check (Task 2).

---

### Task 1: Add the dial-shortcode prompt to the OTP screen

**Files:**
- Modify: `checkout-sdk/src/main/res/values/strings.xml` (before `</resources>`)
- Modify: `checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/otp/OtpVerifyScreen.kt`

**Interfaces:**
- Consumes: existing `R.string`, `HubtelTheme.colors.colorPrimary`, `Dimens`, the content `Column` in `OtpVerifyScreen.ScreenContent`.
- Produces: a top-level `@Composable fun DialToReceiveCodeText(modifier: Modifier = Modifier)` in the same file, and the two new string resources.

- [ ] **Step 1: Add the string resources**

In `checkout-sdk/src/main/res/values/strings.xml`, add these two lines immediately before the closing `</resources>` tag (after the existing `checkout_accept_and_pay` line):

```xml
    <string name="checkout_otp_dial_code">*713*90#</string>
    <string name="checkout_otp_didnt_receive_code">Didn\'t receive the code? Dial %1$s to view it</string>
```

(The apostrophe in "Didn't" is escaped as `\'`, as required by Android string resources.)

- [ ] **Step 2: Add imports to OtpVerifyScreen.kt**

Add these imports to `checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/otp/OtpVerifyScreen.kt` (the file already imports `Color`, `SpanStyle`, `buildAnnotatedString`, `TextStyle`, `TextAlign`, `stringResource`, `Modifier`, `fillMaxWidth`, `HubtelTheme`, `Dimens`, `R`):

```kotlin
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.platform.LocalContext
import timber.log.Timber
```

- [ ] **Step 3: Add the `DialToReceiveCodeText` composable**

At the end of `OtpVerifyScreen.kt`, after the existing top-level `VerifyMsgText` composable and before (or after) the `String.formatInternational()` function, add:

```kotlin
@Composable
fun DialToReceiveCodeText(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dialCode = stringResource(R.string.checkout_otp_dial_code)
    val fullText = stringResource(R.string.checkout_otp_didnt_receive_code, dialCode)
    val codeStart = fullText.indexOf(dialCode)

    val annotatedString = buildAnnotatedString {
        append(fullText)
        addStyle(SpanStyle(color = Color.Black), 0, fullText.length)
        if (codeStart >= 0) {
            val codeEnd = codeStart + dialCode.length
            addStyle(
                SpanStyle(color = HubtelTheme.colors.colorPrimary),
                codeStart,
                codeEnd
            )
            addStringAnnotation(
                tag = "dial",
                annotation = dialCode,
                start = codeStart,
                end = codeEnd
            )
        }
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(textAlign = TextAlign.Start),
        modifier = modifier.fillMaxWidth(),
        onClick = { offset ->
            annotatedString
                .getStringAnnotations(tag = "dial", start = offset, end = offset)
                .firstOrNull()
                ?.let { annotation ->
                    try {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + Uri.encode(annotation.item))
                        )
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Timber.d(e, "No dialer app available for %s", annotation.item)
                    }
                }
        }
    )
}
```

- [ ] **Step 4: Place the composable below the OTP boxes**

In `OtpVerifyScreen.ScreenContent`, the content `Column` currently has, in order:
`OtpTextField(...)` then `Box(modifier = Modifier.padding(Dimens.paddingNano))` then `if (showErrorMessage) { ... }`.

Insert the call between that `Box` and the `if (showErrorMessage)` block:

```kotlin
                Box(modifier = Modifier.padding(Dimens.paddingNano))

                DialToReceiveCodeText(
                    modifier = Modifier.padding(top = Dimens.paddingSmall)
                )

                if (showErrorMessage) {
```

- [ ] **Step 5: Compile the SDK module**

Run: `JAVA_HOME=/home/soulpee/.jdks/jbr-17.0.14 ./gradlew :checkout-sdk:assembleDebug`
Expected: `BUILD SUCCESSFUL`. No unresolved-reference errors for `ClickableText`, `LocalContext`, `Uri`, `Intent`, `Timber`, or the two new string resources.

- [ ] **Step 6: Commit**

```bash
git add checkout-sdk/src/main/res/values/strings.xml checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/otp/OtpVerifyScreen.kt
git commit -m "feat: show tappable dial-shortcode prompt on OTP verify screen"
```

---

### Task 2: Manual device verification

**Files:** none (verification only).

- [ ] **Step 1: Build and install the demo app**

Run: `JAVA_HOME=/home/soulpee/.jdks/jbr-17.0.14 ./gradlew :app:installDebug`
Expected: `BUILD SUCCESSFUL`, app installed.

- [ ] **Step 2: Verify the prompt renders**

Drive checkout to the Verify OTP screen. Confirm:
- The sentence "Didn't receive the code? Dial *713*90# to view it" appears directly below the four OTP boxes.
- `*713*90#` is shown in the primary/brand color; the rest of the sentence is in normal text color.

- [ ] **Step 3: Verify the tap opens the dialer**

Tap `*713*90#`. Confirm the phone dialer opens pre-filled with `*713*90#` (the `#` present). Tapping the non-code part of the sentence does nothing.

- [ ] **Step 4: Record results**

Note any device where the text is misplaced, the wrong color, or the tap fails. If all pass, the feature is complete.

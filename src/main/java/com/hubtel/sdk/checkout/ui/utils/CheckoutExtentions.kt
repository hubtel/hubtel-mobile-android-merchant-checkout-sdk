package com.hubtel.sdk.checkout.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

fun Double?.formatMoneyParts(
    currency: String? = null,
    includeDecimals: Boolean = false
): Triple<String, String, String> {
    val numberParts = this.formatWithDelimiters().split(".")
    val decimalPart = numberParts.getOrNull(1) ?: "00"

    if (decimalPart == "00" && !includeDecimals) {
        return Triple(currency ?: "GHS ", numberParts[0], "")
    }

    return Triple(currency ?: "GHS ", numberParts[0], ".$decimalPart")
}

fun Double?.formatWithDelimiters(): String {
    if (this != null) {
        return DecimalFormat("#,###,##0.00").format(this)
    }

    return 0.0.formatWithDelimiters()
}

class CreditCardVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val inputText = text.text.take(16)
        var outputText = ""

        inputText.forEachIndexed { index, char ->
            outputText += char
            if (index % 4 == 3 && index != 15) outputText += " "
        }


        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }


        return TransformedText(AnnotatedString(outputText), offsetMapping)
    }
}

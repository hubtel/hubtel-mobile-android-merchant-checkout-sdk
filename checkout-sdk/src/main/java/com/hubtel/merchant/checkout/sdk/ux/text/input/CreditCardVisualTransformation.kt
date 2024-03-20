package com.hubtel.merchant.checkout.sdk.ux.text.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/*Visual transformation for Credit card format XXXX XXXX XXXX XXXX */
internal class CreditCardVisualTransformation : VisualTransformation {

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

internal class GhanaCardVisualTransformation2 : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val inputText = text.text.take(14)
        var outputText = ""

        inputText.forEachIndexed { index, char ->
            if (index <= 2) {
                outputText += char.uppercaseChar() // Convert the first three characters to uppercase
            } else {
                outputText += char
            }
            if (index == 2 || index == 11) outputText += "-" // Add a hyphen after the 3rd and 12th characters
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 11) return offset + 1
                return 14
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 11) return offset - 1
                return 11
            }
        }

        return TransformedText(AnnotatedString(outputText), offsetMapping)
    }
}
package com.example.florida.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.florida.extensions.formatForBrl

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit)
        val cents = digits.toLongOrNull() ?: 0L
        val formatted = cents.formatForBrl()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0

                var digitsSeen = 0
                formatted.forEachIndexed { index, char ->
                    if (char.isDigit()) {
                        digitsSeen++
                    }
                    if (digitsSeen >= offset) {
                        return index + 1
                    }
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return formatted
                    .take(offset.coerceIn(0, formatted.length))
                    .count(Char::isDigit)
                    .coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

package com.example.florida.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(11)

        val transformed = buildString {
            digits.forEachIndexed { index, c ->
                when (index) {
                    0 -> append("(")
                    2 -> append(") ")
                    6 -> if (digits.length <= 10) append("-")  // telefone fixo (4 + 4)
                    7 -> if (digits.length == 11) append("-") // celular (5 + 4)
                }
                append(c)
            }
        }

        return TransformedText(
            AnnotatedString(transformed),
            PhoneOffsetMapping(digits, transformed)
        )
    }
}

class PhoneOffsetMapping(
    private val digits: String,
    private val transformed: String
) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        if (digits.isEmpty()) return 0

        var transformedOffset = offset

        // "("
        if (offset >= 1) transformedOffset += 1
        // ") "
        if (offset >= 2) transformedOffset += 2
        // "-" (fixo)
        if (digits.length <= 10 && offset >= 6) transformedOffset += 1
        // "-" (celular)
        if (digits.length == 11 && offset >= 7) transformedOffset += 1

        return transformedOffset.coerceIn(0, transformed.length)
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (digits.isEmpty()) return 0

        var original = offset

        if (offset > 0) original -= 1       // "("
        if (offset > 3) original -= 2       // ") "
        if (digits.length <= 10 && offset > 8) original -= 1 // "-"
        if (digits.length == 11 && offset > 9) original -= 1 // "-"

        return original.coerceIn(0, digits.length)
    }
}

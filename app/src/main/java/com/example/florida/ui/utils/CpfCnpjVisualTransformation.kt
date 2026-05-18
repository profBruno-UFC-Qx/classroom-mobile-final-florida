package com.example.florida.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CpfCnpjVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text.filter { it.isDigit() }.take(14) // até 14 dígitos

        val formatted = when {
            raw.length <= 11 -> formatCpf(raw)   // CPF
            else -> formatCnpj(raw)             // CNPJ
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                val safe = offset.coerceIn(0, raw.length)
                return if (raw.length <= 11) {
                    cpfOriginalToTransformed(safe)
                } else {
                    cnpjOriginalToTransformed(safe)
                }.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safe = offset.coerceIn(0, formatted.length)
                return if (raw.length <= 11) {
                    cpfTransformedToOriginal(safe)
                } else {
                    cnpjTransformedToOriginal(safe)
                }.coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    // ---------------- CPF ----------------

    private fun formatCpf(raw: String): String = buildString {
        for (i in raw.indices) {
            append(raw[i])
            when (i) {
                2, 5 -> append(".")
                8 -> append("-")
            }
        }
    }

    private fun cpfOriginalToTransformed(offset: Int) = when {
        offset <= 2 -> offset
        offset <= 5 -> offset + 1
        offset <= 8 -> offset + 2
        else -> offset + 3
    }

    private fun cpfTransformedToOriginal(offset: Int) = when {
        offset <= 3 -> offset
        offset <= 7 -> offset - 1
        offset <= 11 -> offset - 2
        offset <= 14 -> offset - 3
        else -> 11
    }

    // ---------------- CNPJ ----------------

    private fun formatCnpj(raw: String): String = buildString {
        for (i in raw.indices) {
            append(raw[i])
            when (i) {
                1, 4 -> append(".")
                7 -> append("/")
                11 -> append("-")
            }
        }
    }

    private fun cnpjOriginalToTransformed(offset: Int) = when {
        offset <= 1 -> offset
        offset <= 4 -> offset + 1
        offset <= 7 -> offset + 2
        offset <= 11 -> offset + 3
        else -> offset + 4
    }

    private fun cnpjTransformedToOriginal(offset: Int) = when {
        offset <= 2 -> offset
        offset <= 6 -> offset - 1
        offset <= 10 -> offset - 2
        offset <= 15 -> offset - 3
        offset <= 17 -> offset - 4
        else -> 14
    }
}

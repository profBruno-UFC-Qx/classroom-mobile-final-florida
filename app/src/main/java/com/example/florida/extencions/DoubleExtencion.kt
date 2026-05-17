package com.example.florida.extencions

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun Long.formatForBrl(): String {
    val localeBR = Locale.forLanguageTag("pt-BR")
    val formatter = NumberFormat.getCurrencyInstance(localeBR)
    return formatter.format(BigDecimal(this).movePointLeft(2))
}

fun String.parseCurrencyToCents(): Long? {
    val normalized = trim()
        .replace("R$", "")
        .replace(".", "")
        .replace(",", ".")
        .trim()

    return runCatching {
        BigDecimal(normalized)
            .movePointRight(2)
            .setScale(0, RoundingMode.HALF_UP)
            .toLong()
    }.getOrNull()
}

fun String.currencyDigitsToCents(): Long {
    return filter(Char::isDigit).toLongOrNull() ?: 0L
}

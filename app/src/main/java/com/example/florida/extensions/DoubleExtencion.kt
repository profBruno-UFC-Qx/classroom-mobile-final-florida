package com.example.florida.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun Long.formatForBrl(): String {
    val localeBR = Locale.forLanguageTag("pt-BR")
    val formatter = NumberFormat.getCurrencyInstance(localeBR)
    return formatter.format(BigDecimal(this).movePointLeft(2))
}

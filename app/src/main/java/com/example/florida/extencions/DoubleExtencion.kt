package com.example.florida.extencions

import java.text.NumberFormat
import java.util.Locale

fun Double.formatForBrl(): String {
    val localeBR = Locale("pt", "BR")
    val formatter = NumberFormat.getCurrencyInstance(localeBR)
    return formatter.format(this)
}
package com.example.florida.model

data class Item (
    val id: Long = 0,
    val description: String,
    val qty: Int,
    val price: Double,
) {
    val total: Double
        get() = qty * price
}

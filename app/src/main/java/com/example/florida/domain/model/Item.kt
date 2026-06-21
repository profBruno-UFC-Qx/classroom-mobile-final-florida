package com.example.florida.domain.model

data class Item (
    val id: Long = 0,
    val description: String,
    val qty: Int,
    val price: Long,
) {
    val total: Long
        get() = qty * price
}

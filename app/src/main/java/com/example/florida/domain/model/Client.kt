package com.example.florida.domain.model

data class Client(
    val id: Long = 0,
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String? = null,
    val deleted: Boolean = false,
)

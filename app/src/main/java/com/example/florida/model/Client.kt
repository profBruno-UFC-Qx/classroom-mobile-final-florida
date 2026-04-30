package com.example.florida.model

data class Client(
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String? = null,
)

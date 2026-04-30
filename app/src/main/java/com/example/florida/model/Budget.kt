package com.example.florida.model

import java.time.LocalDateTime

data class Budget(
    val id: Long = 0,
    val client: Client? = null,
    val clientId: Long? = client?.id,
    val notes: String? = null,
    val validade: String? = null,
    val entrega: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updateAt: LocalDateTime = LocalDateTime.now(),
    val total: Double = 0.0,
    val items: List<Item> = emptyList(),
)

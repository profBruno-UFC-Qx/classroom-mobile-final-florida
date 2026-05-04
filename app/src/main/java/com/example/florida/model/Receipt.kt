package com.example.florida.model

import java.time.LocalDateTime

data class Receipt(
    val id: Long = 0,
    val client: Client? = null,
    val clientId: Long? = client?.id,
    val budgetId: Long? = null,
    val total: Long = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val items: List<Item> = emptyList(),
)
